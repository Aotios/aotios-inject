package com.bloder

import com.bloder.dependencies.AotiosInjectScope
import com.bloder.ir.DependencyTransformer
import com.bloder.ir.SummonTransformer
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

internal class AotiosInjectIRExtension(private val debugLogger: DebugLogger) : IrGenerationExtension {

    private val aotiosInjectScope: (IrPluginContext) -> AotiosInjectScope = { context ->
        object : AotiosInjectScope {
            override val pluginContext: IrPluginContext
                get() = context
        }
    }

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.accept(DependencyTransformer(debugLogger), null)
        moduleFragment.transform(SummonTransformer(debugLogger, aotiosInjectScope(pluginContext)), null)
    }
}