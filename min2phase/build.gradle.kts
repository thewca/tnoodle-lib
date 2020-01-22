import configurations.Languages.configureJava
import configurations.Publications.configureBintrayTarget
import configurations.Publications.configureMavenPublication

description = "Chen Shuang's (https://github.com/cs0x7f) awesome 3x3 scrambler built on top of Herbert Kociemba's Java library."

plugins {
    `java-library`
    `maven-publish`
     JFROG_BINTRAY
}

configureJava()
configureMavenPublication("scrambler-min2phase")
configureBintrayTarget()

sourceSets {
    main {
        java {
            exclude("cs/min2phase/MainProgram.java")
        }
    }
}
