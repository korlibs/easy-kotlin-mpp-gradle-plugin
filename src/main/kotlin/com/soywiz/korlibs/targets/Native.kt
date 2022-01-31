package com.soywiz.korlibs.targets

import com.soywiz.korlibs.*
import org.apache.tools.ant.taskdefs.condition.*
import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.jvm.tasks.*
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.mpp.*
import org.jetbrains.kotlin.gradle.targets.native.tasks.*

val linuxEnabled by lazy {
	!Os.isFamily(Os.FAMILY_MAC) && !Os.isFamily(Os.FAMILY_WINDOWS)
}
val isArm get() = listOf("arm", "arm64", "aarch64").any { Os.isArch(it) }

fun Project.configureTargetNative() {
	val nativeExtraJar = tasks.create<Jar>("nativeExtraJar") {
	}

	fun AbstractKotlinTarget.extraNative() {
		mavenPublication(Action { it.artifact(nativeExtraJar) })
		this.attributes.attribute(KotlinPlatformType.attribute, KotlinPlatformType.native)
		compilations.all {
			it.kotlinOptions {
				suppressWarnings = korlibs.supressWarnings
			}
		}
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
		iosSimulatorArm64() {
			extraNative()
		}
		/////////////////////////////////////////
		if (korlibs.tvosEnabled) {
			tvosX64() {
				extraNative()
			}
			tvosArm64() {
				extraNative()
			}
			tvosSimulatorArm64() {
				extraNative()
			}
		}
		/////////////////////////////////////////
		if (korlibs.watchosEnabled) {
			watchosX86() {
				extraNative()
			}
			watchosX64() {
				extraNative()
			}
			watchosArm32() {
				extraNative()
			}
			watchosArm64() {
				extraNative()
			}
			watchosSimulatorArm64() {
				extraNative()
			}
		}
		/////////////////////////////////////////
		macosX64() {
			extraNative()
		}
		macosArm64() {
			extraNative()
		}
		if (linuxEnabled) {
			linuxX64() {
				extraNative()
			}
			if (korlibs.linuxArmEnabled) {
				linuxArm32Hfp() {
					extraNative()
				}
				linuxArm64() {
					extraNative()
				}
			}
		}
		mingwX64() {
			extraNative()
		}
		if (korlibs.androidNativeEnabled) {
			androidNativeArm32() {
				extraNative()
			}
			androidNativeArm64() {
				extraNative()
			}
			androidNativeX86() {
				extraNative()
			}
			androidNativeX64() {
				extraNative()
			}
		}

		if (System.getProperty("idea.version") != null) {
			when {
				Os.isFamily(Os.FAMILY_WINDOWS) -> run { mingwX64("nativeCommon"); mingwX64("nativePosix") }
				Os.isFamily(Os.FAMILY_MAC) -> if (isArm) run {
					macosArm64("nativeCommon"); macosArm64("nativePosix")
				} else run {
					macosX64("nativeCommon"); macosX64("nativePosix")
				}
				else -> run {
					if (linuxEnabled) {
						linuxX64("nativeCommon"); linuxX64("nativePosix")
						if (korlibs.linuxArmEnabled) {
							linuxArm32Hfp("nativeCommon"); linuxArm32Hfp("nativePosix")
							linuxArm64("nativeCommon"); linuxArm64("nativePosix")
						}
					}
				}
			}
		}

		sourceSets.apply {
			dependants("nativeCommon", korlibs.ALL_NATIVE_TARGETS)
			dependants("nativePosix", korlibs.POSIX_NATIVE_TARGETS)
			if (korlibs.NATIVE_POSIX_NON_APPLE_TARGETS.isNotEmpty()) {
				dependants("nativePosixNonApple", korlibs.NATIVE_POSIX_NON_APPLE_TARGETS)
			}
			dependants("mingwCommon", korlibs.WINDOWS_DESKTOP_NATIVE_TARGETS)
			dependants("nativePosixApple", korlibs.NATIVE_POSIX_APPLE_TARGETS)
			dependants("iosCommon", korlibs.IOS_TARGETS)
			dependants("tvosCommon", korlibs.TVOS_TARGETS)
			dependants("watchosCommon", korlibs.WATCHOS_TARGETS)
			dependants("iosWatchosTvosCommon", korlibs.IOS_WATCHOS_TVOS_TARGETS)
			dependants("iosTvosCommon", korlibs.IOS_TVOS_TARGETS)
			dependants("iosWatchosCommon", korlibs.IOS_WATCHOS_TARGETS)
			dependants("macosIosTvosCommon", korlibs.MACOS_IOS_TVOS_TARGETS)
			dependants("macosIosWatchosCommon", korlibs.MACOS_IOS_WATCHOS_TARGETS)
		}
	}

	afterEvaluate {
		for (target in korlibs.DESKTOP_NATIVE_TARGETS + korlibs.IOS_WATCHOS_TVOS_TARGETS) {
			val taskName = "copyResourcesToExecutable_$target"
			val targetTestTask = tasks.findByName("${target}Test") as? KotlinNativeTest? ?: continue
			val compileTestTask = tasks.findByName("compileTestKotlin${target.capitalize()}") ?: continue
			val compileMainTask = tasks.findByName("compileKotlin${target.capitalize()}") ?: continue

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
