package com.soywiz.korlibs.modules

import org.gradle.api.*

class CI(val version: Any, val getEnv: (String) -> String? = { com.soywiz.korlibs.util.getEnv(it) }) {
	constructor(version: Any, envs: Map<String, String>) : this(version, { envs[it] })

	val isSnapshotVersion get() = version.toString().contains("-SNAPSHOT")

	val onGithubActions = getEnv("GITHUB_ACTIONS") == "true"
	val onTravis = getEnv("TRAVIS_BRANCH") != null
	val onCi = onGithubActions || onTravis

	val CI_PULL_REQUEST: Boolean = when {
		onGithubActions -> false
		onTravis -> getEnv("TRAVIS_PULL_REQUEST") == "true"
		else -> false
	}
	val CI_BRANCH: String = when {
		onGithubActions -> (getEnv("GITHUB_REF") ?: "").let { ref -> if (ref.startsWith("refs/heads/")) ref.removePrefix("refs/heads/") else "" }
		onTravis -> getEnv("TRAVIS_BRANCH") ?: ""
		else -> ""
	}
	//val CI_TAG = getEnv("TRAVIS_TAG") ?: ""

	val ciMustPublish = !isSnapshotVersion && !CI_PULL_REQUEST && (CI_BRANCH == "master")
}
