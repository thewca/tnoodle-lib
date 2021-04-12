package configurations

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

import io.github.gradlenexus.publishplugin.NexusPublishExtension

object Publications {
    fun Project.configureMavenPublication(targetArtifactId: String? = null) {
        configure<PublishingExtension> {
            publications {
                create<MavenPublication>(rootProject.name) {
                    targetArtifactId?.let {
                        artifactId = it
                    }

                    from(components["java"])
                }
            }
        }
    }

    fun Project.configureSonatypeNexus() {
        configure<NexusPublishExtension> {
            repositories {
                sonatype {
                    nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
                    snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
                }
            }
        }
    }
}
