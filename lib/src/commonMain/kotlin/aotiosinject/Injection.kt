package aotiosinject

internal fun <T> summon(): T = summon()

class Module {
    fun <T> dependency(resolution: () -> T): T = resolution()
    fun <T> summon(): T = summon()
}

fun module(build: Module.() -> Any) = build(Module())