import configurations.Languages.attachRemoteRepositories
import configurations.Languages.configureJava
import configurations.Frameworks.configureCheckstyle
import configurations.Publications.configureBintrayTarget
import configurations.Publications.configureMavenPublication

description = "A dead simple svg generation library written in pure Java, with no dependencies. This code runs on both desktop Java, Android, and compiles to Javascript with GWT."

attachRemoteRepositories()

plugins {
    `java-library`
    checkstyle
    `maven-publish`
    JFROG_BINTRAY
}

configureJava()
configureCheckstyle()
configureMavenPublication("lib-svglite")
configureBintrayTarget()
