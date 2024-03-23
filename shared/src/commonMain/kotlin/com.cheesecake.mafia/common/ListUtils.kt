package com.cheesecake.mafia.common

interface Item<K: Any> {
    val key: K
}

fun <K: Any, T: Item<K>> List<T>.changeItems(
    keys: List<K>,
    transform: (T) -> T
): List<T> {
    val mutableList = toMutableList()
    keys.forEach { key ->
        val index = mutableList.indexOfFirst { key == it.key }.takeIf { it != -1 }
        index?.let { mutableList[index] = transform(mutableList[index]) }
    }
    return mutableList.toList()
}


fun <K: Any, T: Item<K>> List<T>.changeItem(
    key: K,
    transform: (T) -> T
): List<T> {
    val mutableList = toMutableList()
    val index = mutableList.indexOfFirst { key == it.key }.takeIf { it != -1 }
    index?.let { mutableList[index] = transform(mutableList[index]) }
    return mutableList.toList()
}