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

import android.graphics.Bitmap
import com.indra.slide.networking.ANImageLoader

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */
class LruBitmapCache(maxSize: Int) :
    LruCache<String?, Bitmap?>(maxSize), ANImageLoader.ImageCache {
    override fun sizeOf(key: String?, value: Bitmap?): Int {
        return if (!key.isNullOrEmpty() && value != null){
            value.rowBytes * value.height
        }else{
            1
        }
    }

    override fun getBitmap(key: String): Bitmap {
        return get(key)!!
    }

    override fun evictBitmap(key: String) {
        remove(key)
    }

    override fun evictAllBitmap() {
        evictAll()
    }

    override fun putBitmap(url: String, bitmap: Bitmap) {
        put(url, bitmap)
    }
}