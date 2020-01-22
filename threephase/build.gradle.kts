import configurations.Languages.configureJava
import configurations.Publications.addBintrayTarget
import configurations.Publications.configureMavenPublication

description = "A copy of Chen Shuang's 4x4 scrambler."

plugins {
    `java-library`
    `maven-publish`
}

configureJava()
configureMavenPublication("scrambler-threephase")
addBintrayTarget()

dependencies {
    implementation(project(":min2phase"))
}
