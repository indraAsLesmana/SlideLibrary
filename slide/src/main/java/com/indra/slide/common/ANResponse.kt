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
package com.indra.slide.common

import com.indra.slide.error.ANError
import okhttp3.Response

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */
class ANResponse<T> {
    val result: T?
    val error: ANError?
    var okHttpResponse: Response? = null

    constructor(result: T) {
        this.result = result
        error = null
    }

    constructor(anError: ANError?) {
        result = null
        error = anError
    }

    val isSuccess: Boolean
        get() = error == null

    companion object {
        @JvmStatic
        fun <T> success(result: T): ANResponse<T> {
            return ANResponse(result)
        }

        @JvmStatic
        fun <T> failed(anError: ANError?): ANResponse<T> {
            return ANResponse(anError)
        }
    }
}