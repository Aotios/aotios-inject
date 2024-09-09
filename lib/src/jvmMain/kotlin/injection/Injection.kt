package injection

import aotiosinject.summon

fun <T> summon(): Lazy<T> = lazy { summon() }