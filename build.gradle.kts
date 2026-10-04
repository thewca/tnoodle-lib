allprojects {
    group = "org.worldcubeassociation.tnoodle"
    version = "0.20.0"
}

plugins {
    alias(libs.plugins.nexus.publish)
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        }
    }
}

tasks.register("generateDebugRelease") {
    dependsOn(":scrambles:shadowJar")
}
