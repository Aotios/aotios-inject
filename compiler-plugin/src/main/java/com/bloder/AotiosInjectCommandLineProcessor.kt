package com.bloder

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor

@AutoService(CommandLineProcessor::class)
class AotiosInjectCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = "aotios-inject"
    override val pluginOptions: Collection<CliOption> = listOf()
}
