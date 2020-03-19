package com.soywiz.korlibs.modules

import com.soywiz.korlibs.*
import com.soywiz.korlibs.util.*
import org.gradle.api.*

fun Project.configureCreateVersion() {
	fun command(vararg commands: String) {
		println("Executing... " + commands.joinToString(" "))
		exec {
			it.workingDir(rootDir)
			it.commandLine(*commands)
		}
	}

	fun releaseVersion(version: SemVer) {
		val nextSnapshotVersion = version.withIncrementedVersion().withSnapshot()
		println("Releasing $version ... next snapshot version $nextSnapshotVersion")
		if (shellExec("git", "status", "-s").outputIfNotError.isNotEmpty()) {
			error("Must commit pending changes before releasing a new version")
		}
		val CURRENT_BRANCH = shellExec("git", "rev-parse", "--abbrev-ref", "HEAD").outputIfNotError.trim()
		if (CURRENT_BRANCH != "master") {
			error("Must be in master branch (now git is in '${CURRENT_BRANCH}' branch)")
		}

		command("git", "pull")
		//command("git", "push")

		fun setVersion(version: String) {
			PropertiesUpdater.update(rootDir["gradle.properties"], mapOf("version" to version))
			command("./gradlew") // To refresh versions
		}

		setVersion(version.version)
		command("git", "add", "-A")
		command("git", "commit", "-m", "Release $version")
		val branchName = "releases/$version"
		val tagName = "release-$version"
		command("git", "checkout", "-b", branchName)
		command("git", "tag", "-a", tagName, "-m \"Release $version\"")
		command("git", "push", "--follow-tags", "--set-upstream", "origin", branchName) // Trigger push on master with the release version
		command("git", "checkout", "master")
		setVersion(nextSnapshotVersion.version)
		command("./gradlew") // To refresh versions
		command("git", "add", "-A")
		command("git", "commit", "-m", "Started $nextSnapshotVersion")
		//command("git", "push", "--all", "--tags", "origin")
		command("git", "push", "--follow-tags")
	}

	if (rootProject.tasks.findByName("releaseVersion") == null) {
		rootProject.tasks.create("releaseVersion") { task ->
			task.group = "versioning"
			task.doLast {
				val releaseVersion =
					project.findProperty("nextReleaseVersion") ?: error("Must specify nextReleaseVersion: ./gradlew releaseVersion -PnextReleaseVersion=x.y.z")
				releaseVersion(SemVer(releaseVersion.toString()))
			}
		}

		rootProject.tasks.create("releaseQuickVersion") { task ->
			task.group = "versioning"
			task.doLast {
				releaseVersion(SemVer(version.toString()).withoutSnapshot())
			}
		}
	}
}