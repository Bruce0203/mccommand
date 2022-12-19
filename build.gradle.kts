import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly
typealias Shadowjar = com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val kotlin_version = "1.7.0"
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
    }
}
plugins {

    kotlin("jvm") version "1.7.0"
    id("org.jetbrains.dokka") version "1.4.32"
    id("maven-publish")
}
allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "com.github.johnrengelman.shadow")
    tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveFileName.set("${rootProject.name}.jar")
    }

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.dmulloy2.net/repository/public/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/public/") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://libraries.minecraft.net/") }
        maven { url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/") }

    }

    dependencies {
//    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
        compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")

    }

    lateinit var sourcesArtifact: PublishArtifact


    tasks {
        artifacts {
            sourcesArtifact = archives(jar)
        }
    }

    apply(plugin = "maven-publish")

    publishing {
        val repo = System.getenv("GITHUB_REPOSITORY")
        if (repo === null) return@publishing
        repositories {
            maven {
                url = uri("https://s01.oss.sonatype.org/content/repositories/releases/")
                credentials {

                    username = System.getenv("SONATYPE_USERNAME") as? String
                    password = System.getenv("SONATYPE_PASSWORD") as? String
                }
            }
        }
        publications {
            register<MavenPublication>(project.name) {
                val githubUserName = repo.substring(0, repo.indexOf("/"))
                groupId = "io.github.${githubUserName.toLowerCaseAsciiOnly()}"
                artifactId = project.name.toLowerCase()
                version = System.getenv("GITHUB_BUILD_NUMBER")?: project.version.toString()
                artifact(sourcesArtifact)
            }
        }

    }


}
