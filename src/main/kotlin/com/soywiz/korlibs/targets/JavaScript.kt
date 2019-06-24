package com.soywiz.korlibs.targets

import com.soywiz.korlibs.*
import org.gradle.api.*

fun Project.configureTargetJavaScript() {
    gkotlin.apply {
        js {
            compilations.all {
                it.kotlinOptions.apply {
                    languageVersion = "1.3"
                    sourceMap = true
                    metaInfo = true
                    moduleKind = "umd"
                }
            }
            mavenPublication(Action { publication ->
                //println("JS publication: $publication : ${publication.name}")
            })
            browser {
                testTask {
                    //useMocha() // @TODO: Seems to produce problems where the JS file is produced and consumed
                }
            }
            nodejs {
                testTask {
                    //useMocha() // @TODO: Seems to produce problems where the JS file is produced and consumed
                }
            }
        }
    }

    dependencies.apply {
        add("jsMainImplementation", "org.jetbrains.kotlin:kotlin-stdlib-js")
        add("jsTestImplementation", "org.jetbrains.kotlin:kotlin-test-js")
    }
}

