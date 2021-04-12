import configurations.Languages.configureJava
import configurations.Publications.configureMavenPublication
import configurations.Publications.configureSignatures

description = "Chen Shuang's (https://github.com/cs0x7f) awesome 3x3 scrambler built on top of Herbert Kociemba's Java library."

plugins {
    `java-library`
    `maven-publish`
    signing
}

configureJava()
configureMavenPublication("scrambler-min2phase")
configureSignatures(publishing)

sourceSets {
    main {
        java {
            exclude("cs/min2phase/MainProgram.java")
        }
    }
}
