import configurations.Languages.attachRemoteRepositories
import configurations.Languages.configureJava
import configurations.Publications.configureMavenPublication
import configurations.Publications.configureSignatures

description = "A copy of Chen Shuang's 5x5 scrambler."

plugins {
    `java-library`
    `maven-publish`
    signing
}

configureJava()
configureMavenPublication("scrambler-cube555")
configureSignatures(publishing)

attachRemoteRepositories()

dependencies {
    implementation(libs.logback.classic)
}
