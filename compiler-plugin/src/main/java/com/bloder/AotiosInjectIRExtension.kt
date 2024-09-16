package com.bloder

import com.bloder.ir.DependencyTransformer
import com.bloder.ir.SummonTransformer
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

internal class AotiosInjectIRExtension(private val debugLogger: DebugLogger) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.accept(DependencyTransformer(debugLogger), null)
        moduleFragment.transform(SummonTransformer(debugLogger, pluginContext), null)
    }
}