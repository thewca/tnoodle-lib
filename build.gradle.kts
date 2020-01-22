allprojects {
    group = "org.worldcubeassociation.tnoodle"
    version = "0.16.3"
}

plugins {
    DEPENDENCY_VERSIONS
}

val releasePrefix = "TNoodle-WCA"

tasks.create("generateDebugRelease") {
    dependsOn(":scrambles:shadowJar")
}
