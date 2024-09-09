package com.bloder.dependencies

import org.jetbrains.kotlin.ir.expressions.IrExpression

object Dependencies {

    val dependencies: MutableMap<String, IrExpression> = mutableMapOf()
    val firDependencies: MutableSet<String> = mutableSetOf()
}