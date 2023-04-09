package configurations

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.repositories

object Languages {
    fun Project.attachRemoteRepositories() {
        repositories {
            mavenCentral()
        }
    }

    fun Project.configureJava() {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(11))
            }

            withJavadocJar()
            withSourcesJar()
        }
    }
}
