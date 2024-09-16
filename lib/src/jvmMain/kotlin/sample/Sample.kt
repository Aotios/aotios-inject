package sample

import aotiosinject.module
import injection.summon

class A {
    fun p(): String = "Hellos"
}

private val a by summon<A>()
private val s by lazy {  }

fun m() = module {
    dependency { A() }
    //dependency { B(summon()) }
}

fun main() {
    println(a.p())
}