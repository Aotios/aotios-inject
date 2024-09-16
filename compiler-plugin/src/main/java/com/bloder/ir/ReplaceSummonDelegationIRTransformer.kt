package com.bloder.ir

import com.bloder.DebugLogger
import com.bloder.dependencies.Dependencies
import com.bloder.dependencies.delegationSummonCall
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irBlockBody
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.parent
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.deepCopyWithSymbols
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.extractRelatedDeclaration
import org.jetbrains.kotlin.ir.util.getPackageFragment
import org.jetbrains.kotlin.ir.util.irCall
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames

internal class ReplaceSummonDelegationIRTransformer(
    private val debugLogger: DebugLogger,
    private val pluginContext: IrPluginContext
) : IrElementTransformerVoidWithContext() {

    override fun visitExpression(expression: IrExpression): IrExpression {
        (expression as? IrCall)?.let { irCall ->
            val callId = try { "${expression.symbol.owner.getPackageFragment().packageFqName}.${expression.symbol.owner.name}" } catch (e: Exception) { return expression }
            if (callId != delegationSummonCall) return expression
            debugLogger.log("OLD IR CALL - ${irCall.dump()}")
            val proof = Dependencies.dependencies[expression.typeArguments.firstOrNull()?.classFqName?.toString()] ?: return expression
            return buildDelegation(pluginContext, irCall, proof).deepCopyWithSymbols(initialParent = currentDeclarationParent!!).also {
                debugLogger.log("HUEHUEUE - ${it.dump()}")
            }
            //return proof.deepCopyWithSymbols()
        }
        return super.visitExpression(expression)
    }

    private fun buildDelegation(pluginContext: IrPluginContext, irCall: IrCall, expression: IrExpression): IrExpression {
        val x = pluginContext.referenceFunctions(
            CallableId(StandardNames.BUILT_INS_PACKAGE_FQ_NAME, Name.identifier("lazy"))
        )
        debugLogger.log("LAsZY - ${x.map { it.owner.valueParameters.map { it.name } }}")
        val lazyFunction = pluginContext.referenceFunctions(
            CallableId(StandardNames.BUILT_INS_PACKAGE_FQ_NAME, Name.identifier("lazy"))
        ).first {
            debugLogger.log("type - ${it.owner.valueParameters[0].type.classFqName}")
            it.owner.valueParameters.size == 1
        }
        val newIrCall = irCall(call = irCall, newSymbol = lazyFunction).deepCopyWithSymbols()
        val irBuilder = DeclarationIrBuilder(pluginContext, newIrCall.symbol)
        val lambdaIR = irBuilder.irLambda(
            expression.type,
            pluginContext.symbols.functionN(0).typeWith(expression.type)
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
        val scope = this
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