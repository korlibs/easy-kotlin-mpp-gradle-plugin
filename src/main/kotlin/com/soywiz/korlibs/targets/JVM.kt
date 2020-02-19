package com.soywiz.korlibs.targets

import com.soywiz.korlibs.*
import org.gradle.api.*
import org.gradle.api.tasks.testing.*
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.plugin.*

fun Project.configureTargetJVM() {
    gkotlin.apply {
        jvm {
			this.compilations.all {
				//it.sourceCompatibility = JavaVersion.VERSION_1_8
				//it.targetCompatibility = JavaVersion.VERSION_1_8
				it.kotlinOptions {
					this.jvmTarget = "1.8"
					this.suppressWarnings = korlibs.supressWarnings
				}
			}
			this.attributes.attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
		}
    }

    dependencies.apply {
        add("jvmMainImplementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        add("jvmTestImplementation", "org.jetbrains.kotlin:kotlin-test")
        add("jvmTestImplementation", "org.jetbrains.kotlin:kotlin-test-junit")
    }

    // Headless testing on JVM (so we can use GWT)
    tasks {
        (getByName("jvmTest") as Test).apply {
            jvmArgs = (jvmArgs ?: arrayListOf()) + arrayListOf("-Djava.awt.headless=true")
            testLogging {
                it.exceptionFormat = TestExceptionFormat.FULL
            }
        }
    }
}
