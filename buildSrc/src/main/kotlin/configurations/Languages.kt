package configurations

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.JavaVersion
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType

object Languages {
    fun Project.attachRemoteRepositories() {
        repositories {
            mavenCentral()
        }
    }

    fun Project.configureJava() {
        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8

            withJavadocJar()
            withSourcesJar()
        }

        tasks.withType<JavaCompile> {
            options.release.set(8)
        }
    }
}
