package com.bloder.checker

import com.bloder.DebugLogger
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

internal class FirExtensionRegistrar(
    private val debugLogger: DebugLogger
) : FirExtensionRegistrar() {

    override fun ExtensionRegistrarContext.configurePlugin() {
        +::FirDependencyChecker.bind(debugLogger)
    }
}