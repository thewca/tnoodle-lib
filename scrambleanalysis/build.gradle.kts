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
    implementation("org.apache.commons:commons-math3:3.6.1")
}

configureJUnit5()

application {
    mainClassName = "org.thewca.scrambleanalysis.App"
}
