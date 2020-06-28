package com.soywiz.korlibs.targets

import com.soywiz.korlibs.*
import org.gradle.api.*
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.tasks.*

fun Project.configureTargetJavaScript() {
	//(project.extensions.getByName("node") as NodeExtension).apply {
	//	version = "12.12.0"
	//	//version = "10.16.3"
	//}
    gkotlin.apply {
		js {
			this.attributes.attribute(KotlinPlatformType.attribute, KotlinPlatformType.js)
            compilations.all {
                it.kotlinOptions.apply {
                    languageVersion = "1.3"
                    sourceMap = true
                    metaInfo = true
                    moduleKind = "umd"
					suppressWarnings = korlibs.supressWarnings
                }
            }
            mavenPublication(Action { publication ->
                //println("JS publication: $publication : ${publication.name}")
            })
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

