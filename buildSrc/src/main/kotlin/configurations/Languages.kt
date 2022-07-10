package configurations

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories

object Languages {
    fun Project.attachRemoteRepositories() {
        repositories {
            mavenCentral()
        }
    }

    fun Project.configureJava() {
        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_1_6

            withJavadocJar()
            withSourcesJar()
        }
    }
}
