import configurations.Languages.attachRemoteRepositories
import configurations.Languages.configureJava
import configurations.Publications.configureMavenPublication
import configurations.Publications.configureSignatures

description = "A copy of Chen Shuang's 4x4 scrambler."

plugins {
    `java-library`
    `maven-publish`
    signing
}

configureJava()
configureMavenPublication("scrambler-threephase")
configureSignatures(publishing)

attachRemoteRepositories()

dependencies {
    implementation(project(":min2phase"))
    implementation(libs.slf4j.api)
}
