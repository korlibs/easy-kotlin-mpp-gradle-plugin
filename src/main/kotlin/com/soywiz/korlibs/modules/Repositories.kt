package com.soywiz.korlibs.modules

import org.gradle.api.*

fun Project.configureKorlibsRepos() {
    allprojects {
        repositories.apply {
            mavenLocal().apply {
                content {
                    it.excludeGroup("Kotlin/Native")
                }
            }
            mavenCentral() {
                it.content {
                    it.excludeGroup("Kotlin/Native")
                }
            }
            google().apply {
                content {
                    it.excludeGroup("Kotlin/Native")
                }
            }
        }
    }
}
