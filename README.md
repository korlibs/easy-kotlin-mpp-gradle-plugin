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

apply<com.soywiz.korlibs.KorlibsPlugin>()
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

### Signing

Some useful links and commands: 

* <https://www.albertgao.xyz/2018/01/18/how-to-publish-artifact-to-maven-central-via-gradle/>
* <https://gist.github.com/diegopacheco/13c0754f0ab36482f4f804ef3f05f989>
* <https://www.gnupg.org/gph/en/manual/x56.html>
* <https://docs.gradle.org/current/userguide/signing_plugin.html>
* <https://stackoverflow.com/questions/57921325/gradle-signarchives-unable-to-read-secret-key>
* <https://gist.github.com/kag0/fb6b7ce0816b77fe8349>
* `gpg --full-generate-key`
* `gpg --output public.pgp --armor --export username@email`
* `gpg --armor --export-secret-keys foobar@example.com | awk 'NR == 1 { print "signing.signingKey=" } 1' ORS='\\n'`
