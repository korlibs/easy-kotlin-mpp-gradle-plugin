package com.soywiz.korlibs.util

data class SemVer(val major: Int, val minor: Int?, val patch: Int?, val suffix: String?) {
	val version: String by lazy {
		buildString {
			append("$major")
			if (minor != null) append(".$minor")
			if (patch != null) append(".$patch")
			if (suffix != null) append("-$suffix")
		}
	}

	fun withSnapshot() = copy(suffix = buildString {
		append(suffix ?: "")
		if (!(suffix ?: "").contains("-SNAPSHOT")) {
			append("-SNAPSHOT")
		}
	}.trimStart('-'))

	fun withoutSnapshot() = copy(suffix = suffix?.replace("-SNAPSHOT", ""))

	fun withIncrementedVersion(): SemVer = when {
		patch != null -> copy(patch = patch + 1)
		minor != null -> copy(minor = minor + 1)
		else -> copy(major = major + 1)
	}

	companion object {
		operator fun invoke(version: String): SemVer {
			val parts = version.split('-', limit = 2)
			val versions = parts[0]
			val iversions = versions.split('.').map { it.toIntOrNull() }
			val suffix = parts.getOrNull(1)
			return SemVer(iversions.getOrNull(0) ?: 0, iversions.getOrNull(1), iversions.getOrNull(2), suffix)
		}
	}

	override fun toString(): String = version
}