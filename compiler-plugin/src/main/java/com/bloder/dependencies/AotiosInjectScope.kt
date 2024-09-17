package com.bloder.dependencies

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name

interface AotiosInjectScope {

    val pluginContext: IrPluginContext

    val lazyFunction: IrSimpleFunctionSymbol get() = pluginContext.referenceFunctions(
        CallableId(StandardNames.BUILT_INS_PACKAGE_FQ_NAME, Name.identifier("lazy"))
    ).first {
        it.owner.valueParameters.size == 1
    }

    fun functionN(n: Int): IrClassSymbol = pluginContext.symbols.functionN(n)
}