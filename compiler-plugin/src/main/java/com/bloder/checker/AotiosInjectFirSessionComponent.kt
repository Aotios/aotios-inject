package com.bloder.checker

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.FirSessionComponent
import org.jetbrains.kotlin.fir.SessionConfiguration

class AotiosInjectFirSessionComponent : FirSessionComponent {
    val firDependencies = mutableSetOf<String>()
}

@OptIn(SessionConfiguration::class)
fun registerFirDependenciesComponent(session: FirSession) {
    session.register(AotiosInjectFirSessionComponent::class, AotiosInjectFirSessionComponent())
}

val FirSession.aotiosInjectFirSessionComponent: AotiosInjectFirSessionComponent by FirSession.sessionComponentAccessor()