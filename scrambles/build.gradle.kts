import configurations.Languages.attachRepositories
import configurations.Languages.configureJava
import configurations.Frameworks.configureCheckstyle
import configurations.Frameworks.configureJUnit5
import configurations.Publications.configureMavenPublication
import configurations.Publications.configureSignatures

import dependencies.Libraries.GWTEXPORTER

description = "A Java scrambling suite. Java applications can use this project as a library. A perfect example of this is the webscrambles package."

attachRepositories()

plugins {
    `java-library`
    checkstyle
    `maven-publish`
    signing
    SHADOW
}

configureJava()
configureCheckstyle()
configureMavenPublication("lib-scrambles")
configureSignatures(publishing)

dependencies {
    api(project(":svglite"))

    implementation(project(":min2phase"))
    implementation(project(":threephase"))
    implementation(project(":sq12phase"))

    api(GWTEXPORTER)
}

configureJUnit5()
