package com.bloder.dependencies

import org.jetbrains.kotlin.ir.expressions.IrExpression

val summonCall = "aotiosinject.summon"
val delegationSummonCall = "injection.summon"

object Dependencies {

    val dependencies: MutableMap<String, IrExpression> = mutableMapOf()
    val firDependencies: MutableSet<String> = mutableSetOf()
}