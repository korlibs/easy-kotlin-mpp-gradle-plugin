package com.soywiz.korlibs.modules

import com.soywiz.korlibs.util.*
import org.gradle.api.*
import java.io.*
import java.util.*

val Project.sonatypePublishUserNull: String? get() = (System.getenv("SONATYPE_USERNAME") ?: rootProject.findProperty("SONATYPE_USERNAME")?.toString() ?: project.findProperty("sonatypeUsername")?.toString())
val Project.sonatypePublishPasswordNull: String? get() = (System.getenv("SONATYPE_PASSWORD") ?: rootProject.findProperty("SONATYPE_PASSWORD")?.toString() ?: project.findProperty("sonatypePassword")?.toString())

val Project.sonatypePublishUser get() = sonatypePublishUserNull ?: error("Can't get SONATYPE_USERNAME/sonatypeUsername")
val Project.sonatypePublishPassword get() = sonatypePublishPasswordNull ?: error("Can't get SONATYPE_PASSWORD/sonatypePassword")

fun Project.configureMavenCentralRelease() {
	rootProject.tasks.create("releaseMavenCentral") { task ->
		task.doLast {
			if (!Sonatype.fromProject(project).releaseGroupId(project.group.toString())) {
				error("Can't promote artifacts. Check log for details")
			}
		}
	}
}

open class Sonatype(
	val user: String,
	val pass: String,
	val BASE: String = DEFAULT_BASE
) {
	companion object {
		val DEFAULT_BASE = "https://oss.sonatype.org/service/local/staging"
		private val BASE = DEFAULT_BASE

		fun fromGlobalConfig(): Sonatype {
			val props = Properties().also { it.load(File(System.getProperty("user.home") + "/.gradle/gradle.properties").readText().reader()) }
			return Sonatype(props["sonatypeUsername"].toString(), props["sonatypePassword"].toString(), DEFAULT_BASE)
		}

		fun fromProject(project: Project): Sonatype {
			return Sonatype(project.sonatypePublishUser, project.sonatypePublishPassword)
		}

		@JvmStatic
		fun main(args: Array<String>) {
			val sonatype = fromGlobalConfig()
			sonatype.releaseGroupId("com.soywiz.korlibs")
		}
	}

	fun releaseGroupId(groupId: String = "com.soywiz.korlibs"): Boolean {
		println("Trying to release groupId=$groupId")
		val profileId = findProfileIdByGroupId(groupId)
		println("Determined profileId=$profileId")
		val repositoryId = findProfileRepositories(profileId).firstOrNull()
		if (repositoryId == null) {
			println("Can't find any repositories for profileId=$profileId for groupId=$groupId. Artifacts weren't upload?")
			return false
		}
		var promoted = false
		var stepCount = 0
		loop@while (true) {
			stepCount++
			if (stepCount > 200) {
				error("Too much steps. stepCount=$stepCount")
			}
			val state = try {
				getRepositoryState(repositoryId)
			} catch (e: SimpleHttpException) {
				if (e.responseCode == 404) {
					println("Can't find $repositoryId anymore. Probably released. Stopping")
					break@loop
				} else {
					throw e
				}
			}
			if (state.transitioning) {
				println("Waiting transition $state")
				Thread.sleep(15_000L)
				continue@loop
			}
			when {
				// Even if open, if there are notifications we should drop it
				state.notifications > 0 -> {
					println("Dropping release because of error state.notifications=$state")
					repositoryDrop(repositoryId)
					promoted = false
					break@loop
				}
				state.isOpen -> {
					println("Closing open repository $state")
					repositoryClose(repositoryId)
					Thread.sleep(5_000L)
				}
				else -> {
					println("Promoting repository $state")
					repositoryPromote(repositoryId)
					Thread.sleep(5_000L)
					promoted = true
				}
			}
		}

		return promoted
	}

	open val client = SimpleHttpClient(user, pass)

	fun getRepositoryState(repositoryId: String): RepoState {
		val info = client.request("${BASE}/repository/$repositoryId")
		//println("info: ${info.toStringPretty()}")
		return RepoState(
			type = info["type"].asString,
			notifications = info["notifications"].asInt,
			transitioning = info["transitioning"].asBoolean,
		)
	}

	data class RepoState(
		// "open" or "closed"
		val type: String,
		val notifications: Int,
		val transitioning: Boolean
	) {
		val isOpen get() = type == "open"
	}

	private fun getDataMapForRepository(repositoryId: String): Map<String, Map<*, *>> {
		return mapOf(
			"data" to mapOf(
				"stagedRepositoryIds" to listOf(repositoryId),
				"description" to "",
				"autoDropAfterRelease" to true,
			)
		)
	}

	fun repositoryClose(repositoryId: String) {
		client.request("${BASE}/bulk/close", getDataMapForRepository(repositoryId))
	}

	fun repositoryPromote(repositoryId: String) {
		client.request("${BASE}/bulk/promote", getDataMapForRepository(repositoryId))
	}

	fun repositoryDrop(repositoryId: String) {
		client.request("${BASE}/bulk/drop", getDataMapForRepository(repositoryId))
	}

	fun findProfileRepositories(profileId: String): List<String> {
		return client.request("${BASE}/profile_repositories")["data"].list
			.filter { it["profileId"].asString == profileId }
			.map { it["repositoryId"].asString }
	}

	fun findProfileIdByGroupId(groupId: String): String {
		val profiles = client.request("$BASE/profiles")["data"].list
		return profiles
			.filter { groupId.startsWith(it["name"].asString) }
			.map { it["id"].asString }
			.firstOrNull() ?: error("Can't find profile with group id '$groupId'")
	}

	fun startStagedRepository(profileId: String): String {
		return client.request("${BASE}/profiles/$profileId/start", mapOf(
			"data" to mapOf("description" to "Explicitly created by easy-kotlin-mpp-gradle-plugin")
		))["data"]["stagedRepositoryId"].asString
	}
}

