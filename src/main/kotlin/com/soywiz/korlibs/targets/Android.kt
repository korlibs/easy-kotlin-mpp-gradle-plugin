package com.soywiz.korlibs.targets

import com.soywiz.korlibs.*
import org.gradle.api.*
import org.jetbrains.kotlin.gradle.plugin.*

fun Project.configureTargetAndroid() {
    if (korlibs.hasAndroid) {
        plugins.apply("com.android.library")
        extensions.getByType(com.android.build.gradle.LibraryExtension::class.java).apply {
            compileSdkVersion(project.findProperty("android.compile.sdk.version")?.toString()?.toIntOrNull() ?: 28)
            defaultConfig {
				it.minSdkVersion(project.findProperty("android.min.sdk.version")?.toString()?.toIntOrNull() ?: 16) // Previously 18
                it.targetSdkVersion(project.findProperty("android.target.sdk.version")?.toString()?.toIntOrNull() ?: 28)
            }
        }

/*
As per: 3-Oct-2019
https://github.com/korlibs/klock/issues/66
https://source.android.com/setup/start/build-numbers
Android Name        Android Version       Usage Share     API Level (MIN SDK Version)
Pie                 9                     10.4%           28
Oreo                8.0, 8.1              28.3%↑          26, 27
Nougat              7.0, 7.1              19.2%↓          24, 25
Marshmallow         6.0                   16.9%↓          23
Lollipop            5.0, 5.1              14.5%↓          21, 22
KitKat              4.4                   6.9%↓           19
Jelly Bean          4.1.x, 4.2.x, 4.3.x   3.2%↑           16, 17, 18
Ice Cream Sandwich  4.0.3, 4.0.4          0.3%            14, 15
Gingerbread         2.3.3 to 2.3.7        0.3%↑           9, 19
 */

        gkotlin.apply {
            android {
				//publishLibraryVariants("release")
                //publishLibraryVariants("release", "debug")
				publishAllLibraryVariants()
				//publishLibraryVariantsGroupedByFlavor = true // @TODO: Check. Was this causing problems with Korio?
				this.attributes.attribute(KotlinPlatformType.attribute, KotlinPlatformType.androidJvm)
            }
        }

        dependencies {
            add("androidMainImplementation", "org.jetbrains.kotlin:kotlin-stdlib")
            add("androidTestImplementation", "org.jetbrains.kotlin:kotlin-test")
            add("androidTestImplementation", "org.jetbrains.kotlin:kotlin-test-junit")
        }

		configurations.apply {
			//smokeTest.extendsFrom testImplementation
			this.getAt("androidTestImplementation").extendsFrom(this.getAt("commonMainApi"))
			//androidTestImplementation.extendsFrom(commonMainApi)
		}


	}
}
