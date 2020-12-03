package com.soywiz.korlibs.modules

import com.soywiz.korlibs.*
import groovy.util.*
import groovy.xml.*
import org.gradle.api.*
import org.gradle.api.publish.*
import org.gradle.api.publish.maven.*
import org.gradle.jvm.tasks.Jar

fun Project.configurePublishing() {
    // Publishing
    val publishUser = project.BINTRAY_USER_null
    val publishPassword = project.BINTRAY_KEY_null

    plugins.apply("maven-publish")

    val javadocJar = tasks.create<Jar>("javadocJar") {
        classifier = "javadoc"
    }

	val sourcesJar = tasks.create<Jar>("sourceJar") {
		classifier = "sources"
		val mySourceSet = gkotlin.sourceSets["jvmMain"]
		//val mySourceSet = gkotlin.sourceSets["commonMain"]
		for (dep in mySourceSet.dependsOn + mySourceSet) {
			from(dep.kotlin.srcDirs) {
				it.into(dep.name)
			}
		}
		//from(zipTree((tasks.getByName("jvmSourcesJar") as Jar).outputs))
	}

	//val emptyJar = tasks.create<Jar>("emptyJar") {}

	val publishing = extensions.getByType(PublishingExtension::class.java)
	publishing.apply {
		if (publishUser == null || publishPassword == null) {
			println("Publishing is not enabled. Was not able to determine either `publishUser` or `publishPassword`")
		} else {

			repositories {
				it.maven {
					it.credentials {
						it.username = publishUser
						it.password = publishPassword
					}
					it.url = uri(
						"https://api.bintray.com/maven/${project.property("project.bintray.org")}/${
							project.property("project.bintray.repository")
						}/${project.property("project.bintray.package")}/"
					)
				}
			}
		}
		afterEvaluate {
			//println(gkotlin.sourceSets.names)
			publications.withType(MavenPublication::class.java) { publication ->
				//println("Publication: $publication : ${publication.name} : ${publication.artifactId}")
				if (publication.name == "kotlinMultiplatform") {
					publication.artifact(sourcesJar) {}
					//publication.artifact(emptyJar) {}
				}

				/*
				val sourcesJar = tasks.create<Jar>("sourcesJar${publication.name.capitalize()}") {
					classifier = "sources"
					baseName = publication.name
					val pname = when (publication.name) {
						"metadata" -> "common"
						else -> publication.name
					}
					val names = listOf("${pname}Main", pname)
					val sourceSet = names.mapNotNull { gkotlin.sourceSets.findByName(it) }.firstOrNull() as? KotlinSourceSet

					sourceSet?.let { from(it.kotlin) }
					//println("${publication.name} : ${sourceSet?.javaClass}")

					/*
					doFirst {
						println(gkotlin.sourceSets)
						println(gkotlin.sourceSets.names)
						println(gkotlin.sourceSets.getByName("main"))
						//from(sourceSets.main.allSource)
					}
					afterEvaluate {
						println(gkotlin.sourceSets.names)
					}
					 */
				}
				*/

				//val mustIncludeDocs = publication.name != "kotlinMultiplatform"
				val mustIncludeDocs = true

				//if (publication.name == "")
				if (mustIncludeDocs) {
					publication.artifact(javadocJar)
				}
				publication.pom.withXml {
					it.asNode().apply {
						appendNode("name", project.name)
						appendNode("description", project.property("project.description"))
						appendNode("url", project.property("project.scm.url"))
						appendNode("licenses").apply {
							appendNode("license").apply {
								appendNode("name").setValue(project.property("project.license.name"))
								appendNode("url").setValue(project.property("project.license.url"))
							}
						}
						appendNode("scm").apply {
							appendNode("url").setValue(project.property("project.scm.url"))
						}

						// Workaround for kotlin-native cinterops without gradle metadata
						if (korlibs.cinterops.isNotEmpty()) {
							val dependenciesList = (this.get("dependencies") as NodeList)
							if (dependenciesList.isNotEmpty()) {
								(dependenciesList.first() as Node).apply {
									for (cinterop in korlibs.cinterops.filter { it.targets.contains(publication.name) }) {
										appendNode("dependency").apply {
											appendNode("groupId").setValue("${project.group}")
											appendNode("artifactId").setValue("${project.name}-${publication.name.toLowerCase()}")
											appendNode("version").setValue("${project.version}")
											appendNode("type").setValue("klib")
											appendNode("classifier").setValue("cinterop-${cinterop.name}")
											appendNode("scope").setValue("compile")
											appendNode("exclusions").apply {
												appendNode("exclusion").apply {
													appendNode("artifactId").setValue("*")
													appendNode("groupId").setValue("*")
												}
											}
										}
									}
								}
							}
						}

						// Changes runtime -> compile in Android's AAR publications
						if (publication.pom.packaging == "aar") {
							val nodes = this.getAt(QName("dependencies")).getAt("dependency").getAt("scope")
							for (node in nodes) {
								(node as Node).setValue("compile")
							}
						}
					}
				}
			}
		}
	}
}
