import configurations.Languages.configureJava
import configurations.Publications.configureBintrayTarget
import configurations.Publications.configureMavenPublication

description = "A copy of Chen Shuang's square 1 two phase solver."

plugins {
    `java-library`
    `maven-publish`
    JFROG_BINTRAY
}

configureJava()
configureMavenPublication("scrambler-sq12phase")
configureBintrayTarget()
