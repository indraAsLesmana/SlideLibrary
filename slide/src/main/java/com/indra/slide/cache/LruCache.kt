/*
 * Copyright 2020 indra953@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indra.slide.cache

import android.annotation.SuppressLint
import java.util.*

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */
open class LruCache<K, V>(maxSize: Int) {
    private val map: LinkedHashMap<K, V>
    private var size = 0
    private var maxSize: Int
    private var putCount = 0
    private var createCount = 0
    private var evictionCount = 0
    private var hitCount = 0
    private var missCount = 0
    fun resize(maxSize: Int) {
        require(maxSize > 0) { "maxSize <= 0" }
        synchronized(this) { this.maxSize = maxSize }
        trimToSize(maxSize)
    }

    operator fun get(key: K?): V? {
        if (key == null) {
            throw NullPointerException("key == null")
        }
        var mapValue: V?
        synchronized(this) {
            mapValue = map[key]
            if (mapValue != null) {
                hitCount++
                return mapValue
            }
            missCount++
        }
        val createdValue = create(key) ?: return null
        synchronized(this) {
            createCount++
            mapValue = map.put(key, createdValue)
            if (mapValue != null) { // There was a conflict so undo that last put
                mapValue?.let { map.put(key, it) }
            } else {
                size += safeSizeOf(key, createdValue)
            }
        }
        return if (mapValue != null) {
            entryRemoved(false, key, createdValue, mapValue)
            mapValue
        } else {
            trimToSize(maxSize)
            createdValue
        }
    }

    fun put(key: K?, value: V?): V? {
        if (key == null || value == null) {
            throw NullPointerException("key == null || value == null")
        }
        var previous: V?
        synchronized(this) {
            putCount++
            size += safeSizeOf(key, value)
            previous = map.put(key, value)
            previous?.let {
                size -= safeSizeOf(key, it)
            }

        }
        previous?.let {
            entryRemoved(false, key, it, value)
        }

        trimToSize(maxSize)
        return previous
    }

    fun trimToSize(maxSize: Int) {
        while (true) {
            var key: K? = null
            var value: V? = null
            synchronized(this) {
                check(!(size < 0 || map.isEmpty() && size != 0)) {
                    (javaClass.name
                            + ".sizeOf() is reporting inconsistent results!")
                }
                if (size <= maxSize || map.isEmpty()) {
                    return@synchronized
                }
                val toEvict: Map.Entry<K, V> = map.entries.iterator().next()
                key = toEvict.key
                value = toEvict.value
                map.remove(key?:return)
                size -= safeSizeOf(key?:return, value?:return)
                evictionCount++
            }
            entryRemoved(true, key?:return, value?:return, null)
        }
    }

    fun remove(key: K?): V? {
        if (key == null) {
            throw NullPointerException("key == null")
        }
        var previous: V?
        synchronized(this) {
            previous = map.remove(key)
            if (previous != null) {
                size -= safeSizeOf(key, previous?:return@synchronized)
            }
        }
        if (previous != null) {
            entryRemoved(false, key, previous?:return null, null)
        }
        return previous
    }

    protected fun entryRemoved(
        evicted: Boolean,
        key: K,
        oldValue: V,
        newValue: V?
    ) {
    }

    protected fun create(key: K): V? {
        return null
    }

    private fun safeSizeOf(key: K, value: V): Int {
        val result = sizeOf(key, value)
        check(result >= 0) { "Negative size: $key=$value" }
        return result
    }

    protected open fun sizeOf(key: K, value: V): Int {
        return 1
    }

    fun evictAll() {
        trimToSize(-1)
    }

    @Synchronized
    fun size(): Int {
        return size
    }

    @Synchronized
    fun maxSize(): Int {
        return maxSize
    }

    @Synchronized
    fun hitCount(): Int {
        return hitCount
    }

    @Synchronized
    fun missCount(): Int {
        return missCount
    }

    @Synchronized
    fun createCount(): Int {
        return createCount
    }

    @Synchronized
    fun putCount(): Int {
        return putCount
    }

    @Synchronized
    fun evictionCount(): Int {
        return evictionCount
    }

    @Synchronized
    fun snapshot(): Map<K, V> {
        return LinkedHashMap(map)
    }

    @SuppressLint("DefaultLocale")
    @Synchronized
    override fun toString(): String {
        val accesses = hitCount + missCount
        val hitPercent = if (accesses != 0) 100 * hitCount / accesses else 0
        return String.format(
            "LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]",
            maxSize, hitCount, missCount, hitPercent
        )
    }

    init {
        require(maxSize > 0) { "maxSize <= 0" }
        this.maxSize = maxSize
        map = LinkedHashMap(0, 0.75f, true)
    }
}