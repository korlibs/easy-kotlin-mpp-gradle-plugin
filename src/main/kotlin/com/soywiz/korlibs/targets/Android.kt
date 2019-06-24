package com.soywiz.korlibs.targets

import com.soywiz.korlibs.*
import org.gradle.api.*

fun Project.configureTargetAndroid() {
    if (korlibs.hasAndroid) {
        plugins.apply("com.android.library")
        extensions.getByType(com.android.build.gradle.LibraryExtension::class.java).apply {
            compileSdkVersion(project.findProperty("android.compile.sdk.version")?.toString()?.toIntOrNull() ?: 28)
            defaultConfig {
                it.minSdkVersion(project.findProperty("android.min.sdk.version")?.toString()?.toIntOrNull() ?: 18)
                it.targetSdkVersion(project.findProperty("android.target.sdk.version")?.toString()?.toIntOrNull() ?: 28)
            }
        }

        gkotlin.apply {
            android {
                publishLibraryVariants("release", "debug")
            }
        }

        dependencies {
            add("androidMainImplementation", "org.jetbrains.kotlin:kotlin-stdlib")
            add("androidTestImplementation", "org.jetbrains.kotlin:kotlin-test")
            add("androidTestImplementation", "org.jetbrains.kotlin:kotlin-test-junit")
        }
    }
}
