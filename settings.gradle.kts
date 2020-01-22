rootProject.name = "tnoodle-lib"

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlin-multiplatform") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
        }
    }
}

include("min2phase")
include("scrambles")
include("sq12phase")
include("svglite")
include("threephase")
