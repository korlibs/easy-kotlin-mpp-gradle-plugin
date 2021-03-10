package com.soywiz.korlibs.modules

/*
import com.soywiz.korlibs.util.*
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

fun Project.configureBintrayTools() {
    val projectBintrayRepository by lazy {
        findProperty("project.bintray.repository")?.toString() ?: error("Can't find project.bintray.repository")
    }
    val projectBintrayPackage by lazy {
        findProperty("project.bintray.package")?.toString() ?: error("Can't find project.bintray.package")
    }
    val projectVersion by lazy { project.version.toString() }
    val bintrayUser by lazy { project.BINTRAY_USER }
    val bintrayPass by lazy { project.BINTRAY_KEY }
    val publishingTarget by lazy {
	    findProperty("project.bintray.org")?.toString() ?: bintrayUser
    }

    fun actuallyPublishBintray() {
        println("Trying to publish to bintray $publishingTarget/$projectBintrayRepository/$projectBintrayPackage/$projectVersion...")
        println(
                BintrayTools.publishToBintray(
                        subject = publishingTarget,
                        repo = projectBintrayRepository,
                        _package = projectBintrayPackage,
                        version = projectVersion,
                        user = bintrayUser,
                        pass = bintrayPass
                )
        )
    }

    tasks.create("actuallyPublishBintray") { task ->
		task.group = "publishing"
		//task.dependsOn("publish") // Shouldn't be required with the new workflow
        task.doLast {
			println("Publishing to bintray $publishingTarget/$projectBintrayRepository/$projectBintrayPackage/$projectVersion...")
			actuallyPublishBintray()
        }
    }

	tasks.create("dockerMultiPublishToBintray") { task ->
		task.group = "publishing"
        task.doLast {
			println("Uploading and publishing to bintray $publishingTarget/$projectBintrayRepository/$projectBintrayPackage/$projectVersion...")
			project.exec {
				it.workingDir(rootDir)
				it.setCommandLine(File(rootDir, "gradlew").absolutePath, "publish")
			}
			project.exec {
				it.workingDir(rootDir)
				it.setCommandLine(File(rootDir, "gradlew_win").absolutePath, "publishMingwX64PublicationToMavenRepository")
			}
			actuallyPublishBintray()
			println("Done")
        }
    }
}

val Project.BINTRAY_USER_null
    get() = rootProject.findProperty("BINTRAY_USER")?.toString()
            ?: project.findProperty("bintrayUser")?.toString()
            ?: getEnv("BINTRAY_USER")


val Project.BINTRAY_KEY_null
    get() = rootProject.findProperty("BINTRAY_KEY")?.toString()
            ?: project.findProperty("bintrayApiKey")?.toString()
            ?: getEnv("BINTRAY_API_KEY")
            ?: getEnv("BINTRAY_KEY")

val Project.BINTRAY_USER get() = BINTRAY_USER_null ?: error("Can't determine bintray user")
val Project.BINTRAY_KEY get() = BINTRAY_KEY_null ?: error("Can't determine bintray API_KEY")

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
*/
