package com.bloder.ir

import com.bloder.DebugLogger
import com.bloder.dependencies.AotiosInjectDependencies
import com.bloder.dependencies.AotiosInjectScope
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irBlockBody
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.deepCopyWithSymbols
import org.jetbrains.kotlin.ir.util.getPackageFragment
import org.jetbrains.kotlin.ir.util.irCall
import org.jetbrains.kotlin.name.SpecialNames

internal class ReplaceSummonDelegationIRTransformer(
    private val debugLogger: DebugLogger,
    private val aotiosInjectScope: AotiosInjectScope
) : IrElementTransformerVoidWithContext() {

    override fun visitExpression(expression: IrExpression): IrExpression {
        (expression as? IrCall)?.let { irCall ->
            val callId = try { "${expression.symbol.owner.getPackageFragment().packageFqName}.${expression.symbol.owner.name}" } catch (e: Exception) { return expression }
            if (callId != AotiosInjectDependencies.delegationSummonCall) return expression
            val proof = AotiosInjectDependencies.dependencies[expression.typeArguments.firstOrNull()?.classFqName?.toString()] ?: return expression
            return buildDelegation(irCall, proof).deepCopyWithSymbols(initialParent = currentDeclarationParent!!)
        }
        return super.visitExpression(expression)
    }

    private fun buildDelegation(irCall: IrCall, expression: IrExpression): IrExpression = with(aotiosInjectScope) {
        val lazyFunction = lazyFunction
        val newIrCall = irCall(call = irCall, newSymbol = lazyFunction).deepCopyWithSymbols()
        val lambdaIR = DeclarationIrBuilder(pluginContext, newIrCall.symbol).irLambda(
            expression.type,
            functionN(0).typeWith(expression.type)
        ) {
            +irReturn(expression.deepCopyWithSymbols())
        }
        newIrCall.putValueArgument(0, lambdaIR)
        return newIrCall
    }

    private fun IrBuilderWithScope.irLambda(
        returnType: IrType,
        lambdaType: IrType,
        startOffset: Int = this.startOffset,
        endOffset: Int = this.endOffset,
        block: IrBlockBodyBuilder.() -> Unit,
    ): IrFunctionExpression {
        val lambda = context.irFactory.buildFun {
            this.startOffset = startOffset
            this.endOffset = endOffset
            name = SpecialNames.ANONYMOUS
            this.returnType = returnType
            visibility = DescriptorVisibilities.LOCAL
            origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
        }.apply {
            parent = currentDeclarationParent!!
            val bodyBuilder = DeclarationIrBuilder(context, symbol, startOffset, endOffset)
            valueParameters = listOf()
            body = bodyBuilder.irBlockBody(this) {
                block()
            }
        }
        val function = IrFunctionExpressionImpl(startOffset, endOffset, lambdaType, lambda, IrStatementOrigin.LAMBDA)
        return function
    }
}