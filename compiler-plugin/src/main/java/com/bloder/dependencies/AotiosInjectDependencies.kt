package com.bloder.dependencies


import org.jetbrains.kotlin.ir.expressions.IrExpression

@JvmInline
value class FirDependencies(val value: MutableSet<String>)

object AotiosInjectDependencies {
    val dependencies: MutableMap<String, IrExpression> = mutableMapOf()
    val firDependencies: MutableSet<String> = mutableSetOf()
    val summonCall = "aotiosinject.summon"
    val delegationSummonCall = "injection.summon"
}