import configurations.Languages.configureJava
import configurations.Publications.configureBintrayTarget
import configurations.Publications.configureMavenPublication

description = "A copy of Chen Shuang's 4x4 scrambler."

plugins {
    `java-library`
    `maven-publish`
    JFROG_BINTRAY
}

configureJava()
configureMavenPublication("scrambler-threephase")
configureBintrayTarget()

dependencies {
    implementation(project(":min2phase"))
}
