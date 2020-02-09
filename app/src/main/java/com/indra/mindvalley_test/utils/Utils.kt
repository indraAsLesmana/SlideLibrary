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

package com.indra.mindvalley_test.utils

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Environment
import androidx.core.content.ContextCompat
import java.io.File
import kotlin.math.pow

fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

fun getRootDirPath(context: Context): String? {
    return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        val file: File =
            ContextCompat.getExternalFilesDirs(context.applicationContext, null).get(0)
        file.absolutePath
    } else {
        context.applicationContext.filesDir.absolutePath
    }
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


val Double.byte: Double
get()=this* 1024.0.pow(2.0)

