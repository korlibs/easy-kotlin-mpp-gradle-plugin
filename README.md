## easy-kotlin-mpp-gradle-plugin

Gradle Plugin to handle Kotlin MPP projects easily

<https://bintray.com/korlibs/korlibs/easy-kotlin-mpp-gradle-plugin>

### Usage

If using Gradle Kotlin DSL (`build.gradle.kts`), add:

```
buildscript {
    repositories {
        mavenCentral()
        jcenter()
        google()
        maven { url = uri("https://dl.bintray.com/korlibs/korlibs/") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }

    dependencies {
        classpath("com.soywiz.korlibs:easy-kotlin-mpp-gradle-plugin:<some-version>")
    }
}
```

If using Gradle Groovy DSL (`build.gradle`), add:

```
buildscript {
    repositories {
        mavenCentral()
        jcenter()
        google()
        maven { url "https://dl.bintray.com/korlibs/korlibs/" }
        maven { url "https://plugins.gradle.org/m2/" }
    }

    dependencies {
        classpath("com.soywiz.korlibs:easy-kotlin-mpp-gradle-plugin:<some-version>")
    }
}

apply plugin: com.soywiz.korlibs.KorlibsPlugin
```

Then in `gradle.properties` file define a couple of properties (values are given as example):

```
group=com.soywiz.korlibs.korge
version=2.0.0

project.bintray.org=korlibs
project.bintray.repository=korlibs
project.bintray.package=korge

project.scm.url=https://github.com/korlibs/korge
project.description=Multiplatform Game Engine written in Kotlin
project.license.name=MIT License
project.license.url=https://raw.githubusercontent.com/korlibs/korge/master/LICENSE
```
