package com.bloder.checker

import com.bloder.DebugLogger
import org.jetbrains.kotlin.diagnostics.*
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirCallChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.expressions.FirCall
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.toConeTypeProjection
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.text

internal class AotiosInjectFirDependencyChecker(
    session: FirSession,
    private val debugLogger: DebugLogger
) : FirAdditionalCheckersExtension(session) {

    override val expressionCheckers: ExpressionCheckers = object : ExpressionCheckers() {
        override val callCheckers: Set<FirCallChecker> = setOf(
            DependencyExpressionChecker(debugLogger),
            SummonExpressionChecker(debugLogger)
        )
    }
}

internal class DependencyExpressionChecker(
    private val debugLogger: DebugLogger
) : FirCallChecker(MppCheckerKind.Common) {

    override fun check(expression: FirCall, context: CheckerContext, reporter: DiagnosticReporter) {
        if (expression is FirFunctionCall && expression.calleeReference.name.asString().contains("dependency")) {
            debugLogger.log("Dependency ${expression.source?.text} / ${expression.calleeReference.name.asString()} / ${expression.typeArguments.first().render()}")
            context.session.aotiosInjectFirSessionComponent.firDependencies.add(expression.typeArguments.first().render())
        }
    }
}

internal class SummonExpressionChecker(
    private val debugLogger: DebugLogger
) : FirCallChecker(MppCheckerKind.Common) {

    override fun check(expression: FirCall, context: CheckerContext, reporter: DiagnosticReporter) {
        if (expression is FirFunctionCall && expression.calleeReference.name.asString().contains("summon")) {
            debugLogger.log("Summon ${expression.typeArguments.first().toConeTypeProjection().type} / ${expression.typeArguments.firstOrNull()?.toConeTypeProjection()?.type?.classId}")
            if (!context.session.aotiosInjectFirSessionComponent.firDependencies.contains(expression.typeArguments.first().render())) {
                if (expression.typeArguments.firstOrNull()?.toConeTypeProjection()?.type?.classId == null) return
                reporter.reportOn(
                    expression.source,
                    AotiosFirCheckerError.SUMMON_DEPENDENCY_NOT_FOUND_ERROR,
                    expression.typeArguments.first().render(),
                    context,
                    SourceElementPositioningStrategies.DEFAULT
                )
            }
        }
    }
}