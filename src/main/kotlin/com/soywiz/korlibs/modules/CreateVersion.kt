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
		PropertiesUpdater.update(rootDir["gradle.properties"], mapOf("version" to version.version))
		command("git", "add", "-a")
		command("git", "commit", "-m\"Release $version\"")
		command("git", "tag", "-a", "$version", "-m \"Release $version\"")
		command("git", "push")
		PropertiesUpdater.update(rootDir["gradle.properties"], mapOf("version" to nextSnapshotVersion.version))
		command("git", "add", "gradle.properties")
		command("git", "commit", "-m\"Started $nextSnapshotVersion\"")
		command("git", "push")
	}

	tasks.create("releaseVersion") { task ->
		task.group = "versioning"
		task.doLast {
			val releaseVersion = project.findProperty("nextReleaseVersion") ?: error("Must specify nextReleaseVersion: ./gradlew releaseVersion -PnextReleaseVersion=x.y.z")
			releaseVersion(SemVer(releaseVersion.toString()))
		}
	}

	tasks.create("releaseQuickVersion") { task ->
		task.group = "versioning"
		task.doLast {
			releaseVersion(SemVer(version.toString()).withoutSnapshot())
		}
	}
}