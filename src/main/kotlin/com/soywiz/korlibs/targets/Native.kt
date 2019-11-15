package com.soywiz.korlibs.targets

import com.soywiz.korlibs.*
import org.apache.tools.ant.taskdefs.condition.*
import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.jvm.tasks.*
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.mpp.*
import org.jetbrains.kotlin.gradle.targets.native.tasks.*

val linuxEnabled by lazy { !Os.isFamily(Os.FAMILY_MAC) }

fun Project.configureTargetNative() {
	val nativeExtraJar = tasks.create<Jar>("nativeExtraJar") {
	}

	fun AbstractKotlinTarget.extraNative() {
		mavenPublication(Action { it.artifact(nativeExtraJar) })
		this.attributes.attribute(KotlinPlatformType.attribute, KotlinPlatformType.native)
	}

	gkotlin.apply {
		/////////////////////////////////////////
		iosX64() {
			extraNative()
		}
		iosArm32() {
			extraNative()
		}
		iosArm64() {
			extraNative()
		}
		/////////////////////////////////////////
		tvosX64() {
			extraNative()
		}
		tvosArm64() {
			extraNative()
		}
		/////////////////////////////////////////
		watchosX86() {
			extraNative()
		}
		watchosArm32() {
			extraNative()
		}
		watchosArm64() {
			extraNative()
		}
		/////////////////////////////////////////
		macosX64() {
			extraNative()
		}
		if (linuxEnabled) {
			linuxX64() {
				extraNative()
			}
		}
		mingwX64() {
			extraNative()
		}

		if (System.getProperty("idea.version") != null) {
			when {
				Os.isFamily(Os.FAMILY_WINDOWS) -> run { mingwX64("nativeCommon"); mingwX64("nativePosix") }
				Os.isFamily(Os.FAMILY_MAC) -> run { macosX64("nativeCommon"); macosX64("nativePosix") }
				else -> run {
					if (linuxEnabled) {
						linuxX64("nativeCommon"); linuxX64("nativePosix")
					}
				}
			}
		}

		sourceSets.apply {
			fun dependants(name: String, on: Set<String>) {
				val main = maybeCreate("${name}Main")
				val test = maybeCreate("${name}Test")
				for (o in on) {
					maybeCreate("${o}Main").dependsOn(main)
					maybeCreate("${o}Test").dependsOn(test)
				}
			}

			val none = setOf<String>()
			val android = if (hasAndroid) setOf() else setOf("android")
			val jvm = korlibs.JVM_TARGETS.toSet()
			val js = korlibs.JS_TARGETS.toSet()
			val ios = korlibs.IOS_TARGETS.toSet()
			val macos = korlibs.MACOS_DESKTOP_NATIVE_TARGETS.toSet()
			val linux = korlibs.LINUX_DESKTOP_NATIVE_TARGETS.toSet()
			val mingw = korlibs.WINDOWS_DESKTOP_NATIVE_TARGETS.toSet()
			val apple = ios + macos
			val allNative = apple + linux + mingw
			val jvmAndroid = jvm + android
			val allTargets = allNative + js + jvm + android

			dependants("iosCommon", ios)
			dependants("nativeCommon", allNative)
			dependants("nonNativeCommon", allTargets - allNative)
			dependants("nativePosix", allNative - mingw)
			dependants("nativePosixNonApple", allNative - mingw - apple)
			dependants("nativePosixApple", apple)
			dependants("nonJs", allTargets - js)
		}
	}

	afterEvaluate {
		for (target in korlibs.DESKTOP_NATIVE_TARGETS) {
			val taskName = "copyResourcesToExecutable_$target"
			val targetTestTask = tasks.getByName("${target}Test") as KotlinNativeTest
			val compileTestTask = tasks.getByName("compileTestKotlin${target.capitalize()}")
			val compileMainTask = tasks.getByName("compileKotlin${target.capitalize()}")

			tasks {
				create<Copy>(taskName) {
					for (sourceSet in gkotlin.sourceSets) {
						from(sourceSet.resources)
					}

					into(targetTestTask.executable.parentFile)
				}
			}

			targetTestTask.inputs.files(
				*compileTestTask.outputs.files.files.toTypedArray(),
				*compileMainTask.outputs.files.files.toTypedArray()
			)

			targetTestTask.dependsOn(taskName)
		}
	}
}
