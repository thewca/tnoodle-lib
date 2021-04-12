package dependencies

import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

object Plugins {
    inline val PluginDependenciesSpec.SHADOW_ACTUAL: PluginDependencySpec
        get() = id("com.github.johnrengelman.shadow").version(Versions.Plugins.SHADOW)

    inline val PluginDependenciesSpec.DEPENDENCY_VERSIONS_ACTUAL: PluginDependencySpec
        get() = id("com.github.ben-manes.versions").version(Versions.Plugins.DEPENDENCY_VERSIONS)

    inline val PluginDependenciesSpec.NEXUS_PUBLISH_ACTUAL: PluginDependencySpec
        get() = id("io.github.gradle-nexus.publish-plugin").version(Versions.Plugins.NEXUS_PUBLISH)
}
