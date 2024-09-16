package com.bloder.ir

import com.bloder.DebugLogger
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression

internal class SummonTransformer(
    private val debugLogger: DebugLogger,
    private val pluginContext: IrPluginContext
) : IrElementTransformerVoidWithContext() {

    private val replaceSummonIRTransformer: ReplaceSummonIRTransformer by lazy {
        ReplaceSummonIRTransformer(debugLogger = debugLogger)
    }

    private val replaceSummonDelegationIRTransformer: ReplaceSummonDelegationIRTransformer by lazy {
        ReplaceSummonDelegationIRTransformer(debugLogger = debugLogger, pluginContext = pluginContext)
    }

    override fun visitValueParameterNew(declaration: IrValueParameter): IrStatement {
//        declaration.transform(replaceSummonIRTransformer, null)
        declaration.transform(replaceSummonDelegationIRTransformer, null)
        return super.visitValueParameterNew(declaration)
    }

    override fun visitPropertyNew(declaration: IrProperty): IrStatement {
//        declaration.transform(replaceSummonIRTransformer, null)
        declaration.transform(replaceSummonDelegationIRTransformer, null)
        return super.visitPropertyNew(declaration)
    }

    override fun visitCall(expression: IrCall): IrExpression {
//        expression.transform(replaceSummonIRTransformer, null)
        expression.transform(replaceSummonDelegationIRTransformer, null)
        return super.visitCall(expression)
    }

    override fun visitVariable(declaration: IrVariable): IrStatement {
//        declaration.transform(replaceSummonIRTransformer, null)
        declaration.transform(replaceSummonDelegationIRTransformer, null)
        return super.visitVariable(declaration)
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression): IrExpression {
//        expression.transform(replaceSummonIRTransformer, null)
        expression.transform(replaceSummonDelegationIRTransformer, null)
        return super.visitFunctionExpression(expression)
    }
}