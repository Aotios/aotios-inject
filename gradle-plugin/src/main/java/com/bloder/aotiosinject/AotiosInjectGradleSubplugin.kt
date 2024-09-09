package com.bloder.aotiosinject

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class AotiosInjectGradleSubPlugin : KotlinCompilerPluginSupportPlugin {

    companion object {
        const val SERIALIZATION_GROUP_NAME = "com.bloder"
        const val ARTIFACT_NAME = "compiler-plugin"
        const val VERSION_NUMBER = "0.0.1"
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        return kotlinCompilation.target.project.provider { listOf() }
    }

    override fun getCompilerPluginId(): String = "aotios-inject"

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = SERIALIZATION_GROUP_NAME,
        artifactId = ARTIFACT_NAME,
        version = VERSION_NUMBER
    )
}
