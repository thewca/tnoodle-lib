allprojects {
    group = "org.worldcubeassociation.tnoodle"
    version = "0.16.4"
}

plugins {
    DEPENDENCY_VERSIONS
}

val releasePrefix = "TNoodle-WCA"

tasks.create("generateDebugRelease") {
    dependsOn(":scrambles:shadowJar")
}
