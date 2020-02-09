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
package com.indra.slide.utils

import com.indra.slide.common.ANRequest
import com.indra.slide.common.ResponseType
import okhttp3.Response

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */
object SourceCloseUtil {
    @JvmStatic
    fun close(response: Response?, request: ANRequest<*>) {
        if (request.responseAs !== ResponseType.OK_HTTP_RESPONSE && response != null && response.body() != null && response.body()!!.source() != null
        ) {
            try {
                response.body()!!.source().close()
            } catch (ignore: Exception) {
            }
        }
    }
}