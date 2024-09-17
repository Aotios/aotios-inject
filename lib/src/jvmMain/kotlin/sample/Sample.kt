package sample

import aotiosinject.module
import injection.summon

class A {
    fun p(): String = "Hello"
}

class B(val a: A) {
    fun b(): String = "${a.p()} Bloder"
}

class C(val b: B)

val c by summon<C>()
val b by summon<B>()

object X {
    val b by summon<B>()
}

fun m() = module {
    dependency { A() }
    dependency { B(summon<A>()) }
    dependency { C(summon<B>()) }
}

fun main() {
    println(X.b.b())
}