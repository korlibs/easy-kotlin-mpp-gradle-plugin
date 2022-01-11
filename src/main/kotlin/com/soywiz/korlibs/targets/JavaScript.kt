package com.soywiz.korlibs.targets

import com.soywiz.korlibs.*
import org.gradle.api.*
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask
import org.jetbrains.kotlin.gradle.tasks.*

fun Project.configureTargetJavaScript() {
	//(project.extensions.getByName("node") as NodeExtension).apply {
	//	version = "12.12.0"
	//	//version = "10.16.3"
	//}
    gkotlin.apply {
		rootProject.plugins.withType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin::class.java) {
			rootProject.extensions.findByType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension::class.java)?.nodeVersion =
				rootProject.properties["nodeVersion"]?.toString() ?: "16.9.1"
		}
		// https://blog.jetbrains.com/kotlin/2021/10/control-over-npm-dependencies-in-kotlin-js/
		allprojects {
			tasks.withType(KotlinNpmInstallTask::class.java) {
				it.args += "--ignore-scripts"
			}
		}
		afterEvaluate {
			rootProject.extensions.configure(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension::class.java) {
				it.versions.webpackDevServer.version = "4.0.0"
			}
		}

		js(BOTH) {
			this.attributes.attribute(KotlinPlatformType.attribute, KotlinPlatformType.js)
            compilations.all {
                it.kotlinOptions.apply {
                    sourceMap = true
                    metaInfo = true
                    moduleKind = "umd"
					suppressWarnings = korlibs.supressWarnings
                }
            }
            mavenPublication { publication ->
                //println("JS publication: $publication : ${publication.name}")
            }
            browser {
                testTask {
					useKarma {
						useChromeHeadless()
					}
                }
            }
			if (korlibs.nodejsEnabled) {
				nodejs {
					testTask {
						//useMocha()
					}
				}
			}
        }
    }

	afterEvaluate {
		for (target in korlibs.JS_TARGETS) {
			val taskName = "copyResourcesToExecutable_$target"
			val targetTestTask = tasks.findByName("${target}Test") ?: continue
			val compileTestTask = tasks.findByName("compileTestKotlin${target.capitalize()}") as? Kotlin2JsCompile? ?: continue
			val compileMainTask = tasks.findByName("compileKotlin${target.capitalize()}") ?: continue

			//println("REGISTERED: $taskName, $targetTestTask, $compileTestTask, $compileMainTask")

			tasks {
				create<Copy>(taskName) {
					for (sourceSet in gkotlin.sourceSets) from(sourceSet.resources)
					into(compileTestTask.outputFile.parentFile.parentFile)
				}
			}

			targetTestTask.inputs.files(
				*compileTestTask.outputs.files.files.toTypedArray(),
				*compileMainTask.outputs.files.files.toTypedArray()
			)

			targetTestTask.dependsOn(taskName)
		}
	}

	dependencies.apply {
        add("jsMainImplementation", "org.jetbrains.kotlin:kotlin-stdlib-js")
        add("jsTestImplementation", "org.jetbrains.kotlin:kotlin-test-js")
    }
}

