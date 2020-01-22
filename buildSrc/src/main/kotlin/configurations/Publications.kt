package configurations

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

object Publications {
    fun Project.configureMavenPublication(targetArtifactId: String? = null) {
        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("tnoodle-lib") {
                    targetArtifactId?.let {
                        artifactId = it
                    }

                    from(components["java"])
                }
            }
        }
    }

    fun Project.addBintrayTarget() {
        val rootName = rootProject.name
        val ownName = this.name

        configure<PublishingExtension> {
            repositories {
                maven {
                    name = "JFrogBintray"
                    url = uri("https://api.bintray.com/maven/thewca/$rootName/$ownName/;publish=0")
                    credentials {
                        username = project.findProperty("bintray.user") as? String
                        password = project.findProperty("bintray.key") as? String
                    }
                }
            }
        }
    }
}
