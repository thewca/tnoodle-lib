allprojects {
    group = "org.worldcubeassociation.tnoodle"
    version = "0.18.0"
}

plugins {
    DEPENDENCY_VERSIONS
}

tasks.create("generateDebugRelease") {
    dependsOn(":scrambles:shadowJar")
}
