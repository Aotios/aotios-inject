package sample

import aotiosinject.module
import injection.summon

class A

fun main() {

    module {
        dependency { A() }
    }
}

//val x by summon<A>()
