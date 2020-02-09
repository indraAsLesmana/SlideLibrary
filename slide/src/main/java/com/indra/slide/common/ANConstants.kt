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

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */

object ANConstants {
    const val MAX_CACHE_SIZE = 10 * 1024 * 1024
    const val UPDATE = 0x01
    const val CACHE_DIR_NAME = "cache_an"
    const val CONNECTION_ERROR = "connectionError"
    const val RESPONSE_FROM_SERVER_ERROR = "responseFromServerError"
    const val REQUEST_CANCELLED_ERROR = "requestCancelledError"
    const val PARSE_ERROR = "parseError"
    const val PREFETCH = "prefetch"
    const val FAST_ANDROID_NETWORKING = "FastAndroidNetworking"
    const val USER_AGENT = "User-Agent"
    const val SUCCESS = "success"
    const val OPTIONS = "OPTIONS"
}