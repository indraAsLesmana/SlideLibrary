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
package com.indra.slide.error

import com.indra.slide.common.ANConstants
import com.indra.slide.utils.ParseUtil
import okhttp3.Response

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */
class ANError : Exception {
    var errorBody: String? = null
    var errorCode = 0
    var errorDetail: String? = null
    var response: Response? = null
        private set

    constructor() {}
    constructor(response: Response?) {
        this.response = response
    }

    constructor(message: String?) : super(message) {}
    constructor(message: String?, response: Response?) : super(message) {
        this.response = response
    }

    constructor(message: String?, throwable: Throwable?) : super(
        message,
        throwable
    ) {
    }

    constructor(
        message: String?,
        response: Response?,
        throwable: Throwable?
    ) : super(message, throwable) {
        this.response = response
    }

    constructor(response: Response?, throwable: Throwable?) : super(throwable) {
        this.response = response
    }

    constructor(throwable: Throwable?) : super(throwable) {}

    fun setCancellationMessageInError() {
        errorDetail = ANConstants.REQUEST_CANCELLED_ERROR
    }

    fun <T> getErrorAsObject(objectClass: Class<T>?): T? {
        try {
            return ParseUtil
                .parserFactory
                ?.getObject(errorBody, objectClass) as T
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}