package com.bloder.ir

import com.bloder.DebugLogger
import com.bloder.dependencies.Dependencies
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.getPackageFragment
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import kotlin.collections.firstOrNull
import kotlin.collections.lastOrNull
import kotlin.collections.set

internal class DependencyTransformer(
    private val debugLogger: DebugLogger
) : IrElementVisitorVoid {

    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    override fun visitCall(expression: IrCall) {
        when(expression.symbol.owner.name.asString()) {
            "module" -> transformModuleCall(expression = expression)
            "dependency" -> transformDependencyCall(expression = expression)
        }
    }

    private fun transformModuleCall(expression: IrCall) {
        debugLogger.log("MODULE - ${expression.symbol.owner.getPackageFragment().packageFqName}")
        val arg = expression.valueArguments.firstOrNull()
        (arg as? IrFunctionExpression)?.function?.body?.statements?.forEach { statement ->
            (statement as? IrTypeOperatorCall)?.acceptChildrenVoid(this)
            (statement as? IrReturn)?.acceptChildrenVoid(this)
        }
    }

    private fun transformDependencyCall(expression: IrCall) {
        debugLogger.log("DEPENDENCY - ${expression.symbol.owner.getPackageFragment().packageFqName}")
        val functionExpression = (expression.valueArguments.firstOrNull() as? IrFunctionExpression)?.function
        val returnExpression = (functionExpression?.body?.statements?.lastOrNull() as? IrReturn)?.value ?: return
        expression.typeArguments.firstOrNull()?.classFqName?.toString()?.let { name ->
            Dependencies.dependencies[name] = returnExpression
        }
    }
}