package com.bloder.checker

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.Renderer
import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
import org.jetbrains.kotlin.psi.KtElement

object AotiosFirCheckerError : BaseDiagnosticRendererFactory() {

    init {
        RootDiagnosticRendererFactory.registerFactory(AotiosFirCheckerError)
    }

    val SUMMON_DEPENDENCY_NOT_FOUND_ERROR by error1<KtElement, String>()

    override val MAP: KtDiagnosticFactoryToRendererMap = rendererMap { map ->
        map.put(
            factory = SUMMON_DEPENDENCY_NOT_FOUND_ERROR,
            message = "No dependency declared for `{0}` type",
            rendererA = Renderer { t: String -> t }
        )
    }

    private fun rendererMap(
        block: (KtDiagnosticFactoryToRendererMap) -> Unit
    ): KtDiagnosticFactoryToRendererMap = KtDiagnosticFactoryToRendererMap("AotiosFirCheckerError").also(block)
}