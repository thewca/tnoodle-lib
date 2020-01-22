package configurations

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.delegateClosureOf
import org.gradle.kotlin.dsl.get

import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayExtension.PackageConfig

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

    fun Project.configureBintrayTarget() {
        configure<BintrayExtension> {
            user = findProperty("bintray.user") as? String
            key = findProperty("bintray.key") as? String
            publish = false

            setPublications(rootProject.name)

            pkg(delegateClosureOf<PackageConfig> {
                repo = rootProject.name
                name = project.name
                websiteUrl = "https://worldcubeassociation.org"
                vcsUrl = "https://github.com/thewca/tnoodle-lib"
                setLabels("java")
                setLicenses("GPL-3.0")
            })
        }
    }
}
