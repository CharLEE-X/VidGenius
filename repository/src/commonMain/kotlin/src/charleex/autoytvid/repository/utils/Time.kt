package com.hackathon.cda.utils

inline fun <T> measureTimeMillis(loggingFunction: (Long) -> Unit,
                                 function: () -> T): T {

    val startTime = System.currentTimeMillis()
    val result: T = function.invoke()
    loggingFunction.invoke(System.currentTimeMillis() - startTime)

    return result
}

inline fun<T> measureTimeMillisPair(function: () -> T): Pair<T, Long> {
    val startTime = System.currentTimeMillis()
    val result: T = function.invoke()
    val endTime = System.currentTimeMillis()

    return Pair(result, endTime - startTime)
}
