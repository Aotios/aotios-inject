package sample

import aotiosinject.module
import injection.summon

//import injection.summon

class A
fun <T> test(initializer: () -> T): Lazy<T> = lazy(initializer)

val y by test { A() }
val x by summon<A>()

fun main() {

    module {
        dependency { A() }
    }
}