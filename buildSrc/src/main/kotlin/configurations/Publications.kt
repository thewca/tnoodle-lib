package configurations

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.plugins.signing.SigningExtension

object Publications {
    fun Project.configureMavenPublication(targetArtifactId: String? = null) {
        configure<PublishingExtension> {
            publications {
                create<MavenPublication>(derivePublicationName()) {
                    targetArtifactId?.let {
                        artifactId = it
                    }

                    from(components["java"])

                    pom {
                        name.set(targetArtifactId)
                        description.set(project.description)
                        url.set("https://www.worldcubeassociation.org/regulations/scrambles/")
                        licenses {
                            license {
                                name.set("GPL-v3.0")
                                url.set("https://www.gnu.org/licenses/gpl-3.0.txt")
                            }
                        }
                        developers {
                            developer {
                                id.set("thewca")
                                name.set("WCA Software Team")
                                email.set("software@worldcubeassociation.org")
                            }
                        }
                        scm {
                            connection.set("scm:git:git://github.com/thewca/tnoodle-lib.git")
                            developerConnection.set("scm:git:git@github.com:thewca/tnoodle-lib.git")
                            url.set("https://github.com/thewca/tnoodle-lib")
                        }
                    }
                }
            }
        }
    }

    fun Project.configureSignatures(publication: PublishingExtension) {
        configure<SigningExtension> {
            sign(publication.publications[derivePublicationName()])
        }
    }

    private fun Project.derivePublicationName() = rootProject.name
}
