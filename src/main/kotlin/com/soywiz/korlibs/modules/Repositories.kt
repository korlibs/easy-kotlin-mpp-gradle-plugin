package com.soywiz.korlibs.modules

import com.soywiz.korlibs.korlibs
import org.gradle.api.*

fun Project.configureKorlibsRepos() {
    allprojects {
        repositories.apply {
            mavenLocal().apply {
                content {
                    it.excludeGroup("Kotlin/Native")
                }
            }
			if (korlibs.isKotlinDev || korlibs.isKotlinEap) {
				maven {
					it.url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
				}
				maven {
					it.url = uri("https://dl.bintray.com/kotlin/kotlin-dev")
				}
			}
            maven {
                it.url = uri("https://dl.bintray.com/korlibs/korlibs")
                it.content {
                    it.excludeGroup("Kotlin/Native")
                }
            }
            jcenter() {
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
