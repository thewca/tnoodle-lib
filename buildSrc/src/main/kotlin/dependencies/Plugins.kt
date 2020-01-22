package dependencies

import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

object Plugins {
    inline val PluginDependenciesSpec.SHADOW_ACTUAL: PluginDependencySpec
        get() = id("com.github.johnrengelman.shadow").version(Versions.Plugins.SHADOW)

    inline val PluginDependenciesSpec.DEPENDENCY_VERSIONS_ACTUAL: PluginDependencySpec
        get() = id("com.github.ben-manes.versions").version(Versions.Plugins.DEPENDENCY_VERSIONS)

    inline val PluginDependenciesSpec.GIT_VERSION_TAG_ACTUAL: PluginDependencySpec
        get() = id("com.palantir.git-version").version(Versions.Plugins.GIT_VERSION_TAG)

    inline val PluginDependenciesSpec.JFROG_BINTRAY_ACTUAL: PluginDependencySpec
        get() = id("com.jfrog.bintray")
}
