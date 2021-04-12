import configurations.Languages.attachRemoteRepositories
import configurations.Languages.configureJava
import configurations.Publications.configureMavenPublication
import configurations.Publications.configureSignatures
import dependencies.Libraries.LOGBACK_CLASSIC

description = "A copy of Chen Shuang's square 1 two phase solver."

plugins {
    `java-library`
    `maven-publish`
    signing
}

configureJava()
configureMavenPublication("scrambler-sq12phase")
configureSignatures(publishing)

attachRemoteRepositories()

dependencies {
    implementation(LOGBACK_CLASSIC)
}
