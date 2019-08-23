package com.soywiz.korlibs.modules

import org.apache.tools.ant.taskdefs.condition.*
import org.gradle.api.*
import java.net.*
import java.util.*

fun Project.configureBintrayTools() {
	val projectBintrayOrg by lazy { findProperty("project.bintray.org")?.toString() ?: error("Can't find project.bintray.org") }
	val projectBintrayRepository by lazy { findProperty("project.bintray.repository")?.toString() ?: error("Can't find project.bintray.repository") }
	val projectBintrayPackage by lazy { findProperty("project.bintray.package")?.toString() ?: error("Can't find project.bintray.package") }
	val projectVersion by lazy { project.version.toString() }
	val bintrayUser by lazy {
		(rootProject.findProperty("BINTRAY_USER") ?: project.findProperty("bintrayUser") ?: System.getenv("BINTRAY_USER"))?.toString()
			?: error("Can't determine bintray user")
	}
	val bintrayPass by lazy {
		(rootProject.findProperty("BINTRAY_KEY") ?: project.findProperty("bintrayApiKey") ?: System.getenv("BINTRAY_API_KEY"))?.toString()
			?: error("Can't determine bintray API_KEY")
	}

	fun actuallyPublishBintray() {
		println("Trying to publish to bintray $projectBintrayOrg/$projectBintrayRepository/$projectBintrayPackage/$projectVersion...")
		println(
			BintrayTools.publishToBintray(
				subject = projectBintrayOrg,
				repo = projectBintrayRepository,
				_package = projectBintrayPackage,
				version = projectVersion,
				user = bintrayUser,
				pass = bintrayPass
			)
		)
	}

	tasks.create("actuallyPublishBintray") {
		it.doLast {
			actuallyPublishBintray()
		}
	}

	tasks.create("localPublishToBintrayIfRequired") {
		if (project.version.toString().contains("-SNAPSHOT")) {
			println("NOT Publishing to bintray $projectBintrayOrg/$projectBintrayRepository/$projectBintrayPackage/$projectVersion... (since it has -SNAPSHOT in its version)")
		} else {
			println("Publishing to bintray $projectBintrayOrg/$projectBintrayRepository/$projectBintrayPackage/$projectVersion...")
			project.exec {
				it.workingDir(rootDir)
				if (Os.isFamily(Os.FAMILY_WINDOWS)) {
					it.setCommandLine("./gradlew", "publishMingwX64PublicationToMavenRepository")
				} else {
					it.setCommandLine("./gradlew", "publish")
				}
			}
		}
	}

	tasks.create("dockerMultiPublishToBintray") {
		it.doLast {
			if (project.version.toString().contains("-SNAPSHOT")) {
				println("NOT Publishing to bintray $projectBintrayOrg/$projectBintrayRepository/$projectBintrayPackage/$projectVersion... (since it has -SNAPSHOT in its version)")
			} else {
				println("Publishing to bintray $projectBintrayOrg/$projectBintrayRepository/$projectBintrayPackage/$projectVersion...")
				project.exec {
					it.workingDir(rootDir)
					it.setCommandLine("./gradlew", "publish")
				}
				project.exec {
					it.workingDir(rootDir)
					it.setCommandLine("./gradlew_win", "publishMingwX64PublicationToMavenRepository")
				}
				actuallyPublishBintray()
				println("Done")
			}
		}
	}
}

object BintrayTools {
	// https://bintray.com/docs/api/#_publish_discard_uploaded_content
	// POST /content/:subject/:repo/:package/:version/publish
	//{
	//	...optional signing details...
	//	"discard": true,
	//	"publish_wait_for_secs": -1
	//}
	fun publishToBintray(
		subject: String,
		repo: String,
		_package: String,
		version: String,
		user: String,
		pass: String
	): String {
		return sendAuthPostRequest(
			"https://bintray.com/api/v1/content/$subject/$repo/$_package/$version/publish",
			"{\"discard\": false, \"publish_wait_for_secs\": -1}".toByteArray(Charsets.UTF_8),
			user, pass
		).toString(Charsets.UTF_8)
	}

	fun ByteArray.toBase64(): String = Base64.getEncoder().encodeToString(this)

	fun sendAuthPostRequest(url: String, body: ByteArray, user: String, password: String): ByteArray {
		return with(URL(url).openConnection() as HttpURLConnection) {
			//println("[1]")
			requestMethod = "POST"
			setRequestProperty("User-Agent", "Mozilla/5.0")
			setRequestProperty("Accept-Language", "en-US,en;q=0.5")
			val encodedAuth = "$user:$password".toByteArray(Charsets.UTF_8).toBase64()
			setRequestProperty("Authorization", "Basic $encodedAuth")
			//Authorization: Basic QWxhZGRpbjpPcGVuU2VzYW1l
			//println("[2]")
			doOutput = true
			outputStream.write(body)
			outputStream.flush()
			outputStream.close()
			//println("[3]")
			//println("responseCode: $responseCode")
			if (this.responseCode >= 400) {
				error("Error replied with $responseCode : " + errorStream.readBytes().toString(Charsets.UTF_8))
			} else {
				inputStream.readBytes()
			}
		}
	}
}
