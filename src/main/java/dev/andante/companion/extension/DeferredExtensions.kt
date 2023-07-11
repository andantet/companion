package dev.andante.companion.extension

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

@OptIn(DelicateCoroutinesApi::class)
fun <T, R> Deferred<T>.asyncApplyOnCompletion(action: (T) -> R): Deferred<R> {
    val deferred = this
    return GlobalScope.async {
        val value = deferred.await()
        return@async action(value)
    }
}
