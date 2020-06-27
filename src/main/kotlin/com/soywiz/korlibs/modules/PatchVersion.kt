package com.soywiz.korlibs.modules

import org.gradle.api.*

fun Project.configurePatchVersion() {
	allprojects {
		val forcedVersion = System.getenv("FORCED_KORGE_PLUGINS_VERSION")
		project.version = forcedVersion?.removePrefix("refs/tags/v")?.removePrefix("v") ?: project.version
	}
}

