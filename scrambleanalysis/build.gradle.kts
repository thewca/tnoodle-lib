import configurations.Languages.attachRemoteRepositories
import configurations.Frameworks.configureJUnit5
import configurations.Languages.configureJava

description = "Scramble quality checker that performs statistical analyses"

attachRemoteRepositories()

plugins {
    java
    application
}

configureJava()

dependencies {
    implementation(project(":scrambles"))
    implementation(project(":min2phase"))
    implementation(libs.apache.commons.math3)
}

configureJUnit5()

application {
    mainClass.set("org.thewca.scrambleanalysis.App")
}
