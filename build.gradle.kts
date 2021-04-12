import configurations.Publications.configureSonatypeNexus

allprojects {
    group = "org.worldcubeassociation.tnoodle"
    version = "0.18.0"
}

plugins {
    DEPENDENCY_VERSIONS
    NEXUS_PUBLISH
}

configureSonatypeNexus()

tasks.create("generateDebugRelease") {
    dependsOn(":scrambles:shadowJar")
}
