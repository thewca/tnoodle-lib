import configurations.Frameworks.configureJUnit5
import configurations.Languages.attachRemoteRepositories
import configurations.Languages.configureJava
import configurations.Publications.configureMavenPublication
import configurations.Publications.configureSignatures

description = "A three-phase solver for the Face Turning Octahedron."

plugins {
    `java-library`
    `maven-publish`
    signing
}

configureJava()
configureMavenPublication("scrambler-fto3phase")
configureSignatures(publishing)

attachRemoteRepositories()

configureJUnit5()
