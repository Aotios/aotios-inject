package com.bloder

import aotiosinject.module
import injection.summon

class A
class B(val a: A)

fun module() = module {
    dependency { A() }
    dependency { B(summon()) }
}

private val b by summon<B>()

fun main() {
    println("Hello World!")
}