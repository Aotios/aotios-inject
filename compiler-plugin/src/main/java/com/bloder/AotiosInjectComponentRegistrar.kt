package com.bloder

import com.bloder.checker.AotiosInjectFirExtensionRegistrar
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@AutoService(CompilerPluginRegistrar::class)
class AotiosInjectComponentRegistrar : CompilerPluginRegistrar() {

    override val supportsK2: Boolean
        get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        FirExtensionRegistrarAdapter.registerExtension(AotiosInjectFirExtensionRegistrar(DebugLogger(messageCollector)))
        IrGenerationExtension.registerExtension(AotiosInjectIRExtension(DebugLogger(messageCollector)))
    }
}
