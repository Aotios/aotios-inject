package com.bloder.ir

import com.bloder.DebugLogger
import com.bloder.dependencies.Dependencies
import com.bloder.dependencies.summonCall
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.deepCopyWithSymbols
import org.jetbrains.kotlin.ir.util.getPackageFragment
import kotlin.collections.get

internal class ReplaceSummonIRTransformer(
    private val debugLogger: DebugLogger
) : IrElementTransformerVoidWithContext() {

    override fun visitExpression(expression: IrExpression): IrExpression {
        (expression as? IrCall)?.let { irCall ->
            val callId = "${expression.symbol.owner.getPackageFragment().packageFqName}.${expression.symbol.owner.name}"
            if (callId != summonCall) return expression
            val proof = Dependencies.dependencies[expression.typeArguments.firstOrNull()?.classFqName?.toString()] ?: return expression
            return proof.deepCopyWithSymbols()
        }
        return super.visitExpression(expression)
    }
}