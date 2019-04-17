package dk.kea.androidgame.martin.myfirstgameengine.engine.core

import java.util.*

abstract class Pool<T> {
    private val items = ArrayList<T>()

    /**
     * Adds a new item to the list of items.
     *
     * @return T item type
     */
    protected abstract fun newItem(): T

    fun obtains(): T {
        val size = items.size
        return if (size == 0) {
            newItem()
        } else items.removeAt(size - 1)
    }

    fun free(item: T) {
        items.add(item)
    }
}
