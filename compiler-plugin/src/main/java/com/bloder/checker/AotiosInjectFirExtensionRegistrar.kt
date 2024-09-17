package com.bloder.checker

import com.bloder.DebugLogger
import com.bloder.dependencies.AotiosInjectDependencies
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.FirSessionComponent
import org.jetbrains.kotlin.fir.SessionConfiguration
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

internal class AotiosInjectFirExtensionRegistrar(
    private val debugLogger: DebugLogger
) : FirExtensionRegistrar() {

    override fun ExtensionRegistrarContext.configurePlugin() {
        { session: FirSession ->
            registerFirDependenciesComponent(session = session)
            AotiosInjectFirDependencyChecker(session, debugLogger)
        }.unaryPlus()
        debugLogger.log("All FIR stuffs finished")
        AotiosInjectDependencies.firDependencies.clear()
    }
}
