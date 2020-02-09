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

import com.google.gson.Gson
import com.indra.slide.gsonparserfactory.GsonParserFactory
import com.indra.slide.interfaces.Parser

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */

object ParseUtil {
    private var mParserFactory: Parser.Factory? = null

    @JvmStatic
    var parserFactory: Parser.Factory?
        get() {
            if (mParserFactory == null) {
                mParserFactory = GsonParserFactory(Gson())
            }
            return mParserFactory
        }
        set(parserFactory) {
            mParserFactory = parserFactory
        }

    @JvmStatic
    fun shutDown() {
        mParserFactory = null
    }
}