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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView.ScaleType
import com.indra.slide.common.ANConstants
import com.indra.slide.common.ANRequest
import com.indra.slide.common.ANResponse
import com.indra.slide.common.ANResponse.Companion.failed
import com.indra.slide.common.ANResponse.Companion.success
import com.indra.slide.error.ANError
import okhttp3.Cache
import okhttp3.Response
import okio.Okio
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URLConnection

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */
object Utils {
    fun getDiskCacheDir(
        context: Context,
        uniqueName: String?
    ): File {
        return File(context.cacheDir, uniqueName)
    }

    @JvmStatic
    fun getCache(
        context: Context,
        maxCacheSize: Int,
        uniqueName: String?
    ): Cache {
        return Cache(
            getDiskCacheDir(context, uniqueName),
            maxCacheSize.toLong()
        )
    }

    fun getMimeType(path: String?): String {
        val fileNameMap = URLConnection.getFileNameMap()
        var contentTypeFor = fileNameMap.getContentTypeFor(path)
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream"
        }
        return contentTypeFor
    }

    @JvmStatic
    fun decodeBitmap(
        response: Response, maxWidth: Int,
        maxHeight: Int, decodeConfig: Bitmap.Config?,
        scaleType: ScaleType
    ): ANResponse<Bitmap> {
        return decodeBitmap(
            response, maxWidth, maxHeight, decodeConfig,
            BitmapFactory.Options(), scaleType
        )
    }

    fun decodeBitmap(
        response: Response, maxWidth: Int,
        maxHeight: Int, decodeConfig: Bitmap.Config?,
        decodeOptions: BitmapFactory.Options,
        scaleType: ScaleType
    ): ANResponse<Bitmap> {
        var data = ByteArray(0)
        try {
            data = Okio.buffer(response.body()!!.source()).readByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var bitmap: Bitmap? = null
        if (maxWidth == 0 && maxHeight == 0) {
            decodeOptions.inPreferredConfig = decodeConfig
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.size, decodeOptions)
        } else {
            decodeOptions.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(data, 0, data.size, decodeOptions)
            val actualWidth = decodeOptions.outWidth
            val actualHeight = decodeOptions.outHeight
            val desiredWidth = getResizedDimension(
                maxWidth, maxHeight,
                actualWidth, actualHeight, scaleType
            )
            val desiredHeight = getResizedDimension(
                maxHeight, maxWidth,
                actualHeight, actualWidth, scaleType
            )
            decodeOptions.inJustDecodeBounds = false
            decodeOptions.inSampleSize = findBestSampleSize(
                actualWidth,
                actualHeight,
                desiredWidth,
                desiredHeight
            )
            val tempBitmap =
                BitmapFactory.decodeByteArray(data, 0, data.size, decodeOptions)
            if (tempBitmap != null && (tempBitmap.width > desiredWidth ||
                        tempBitmap.height > desiredHeight)
            ) {
                bitmap = Bitmap.createScaledBitmap(
                    tempBitmap,
                    desiredWidth, desiredHeight, true
                )
                tempBitmap.recycle()
            } else {
                bitmap = tempBitmap
            }
        }
        return bitmap?.let { success(it) }
            ?: failed(
                getErrorForParse(
                    ANError(
                        response
                    )
                )
            )
    }

    private fun getResizedDimension(
        maxPrimary: Int, maxSecondary: Int,
        actualPrimary: Int, actualSecondary: Int,
        scaleType: ScaleType
    ): Int {
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary
        }
        if (scaleType == ScaleType.FIT_XY) {
            return if (maxPrimary == 0) {
                actualPrimary
            } else maxPrimary
        }
        if (maxPrimary == 0) {
            val ratio =
                maxSecondary.toDouble() / actualSecondary.toDouble()
            return (actualPrimary * ratio).toInt()
        }
        if (maxSecondary == 0) {
            return maxPrimary
        }
        val ratio = actualSecondary.toDouble() / actualPrimary.toDouble()
        var resized = maxPrimary
        if (scaleType == ScaleType.CENTER_CROP) {
            if (resized * ratio < maxSecondary) {
                resized = (maxSecondary / ratio).toInt()
            }
            return resized
        }
        if (resized * ratio > maxSecondary) {
            resized = (maxSecondary / ratio).toInt()
        }
        return resized
    }

    fun findBestSampleSize(
        actualWidth: Int, actualHeight: Int,
        desiredWidth: Int, desiredHeight: Int
    ): Int {
        val wr = actualWidth.toDouble() / desiredWidth
        val hr = actualHeight.toDouble() / desiredHeight
        val ratio = Math.min(wr, hr)
        var n = 1.0f
        while (n * 2 <= ratio) {
            n *= 2f
        }
        return n.toInt()
    }

    @JvmStatic
    @Throws(IOException::class)
    fun saveFile(
        response: Response, dirPath: String?,
        fileName: String?
    ) {
        var `is`: InputStream? = null
        val buf = ByteArray(2048)
        var len: Int
        var fos: FileOutputStream? = null
        try {
            `is` = response.body()!!.byteStream()
            val dir = File(dirPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, fileName)
            fos = FileOutputStream(file)
            while (`is`.read(buf).also { len = it } != -1) {
                fos.write(buf, 0, len)
            }
            fos.flush()
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun getErrorForConnection(error: ANError): ANError {
        error.errorDetail = ANConstants.CONNECTION_ERROR
        error.errorCode = 0
        return error
    }

    @JvmStatic
    fun getErrorForServerResponse(error: ANError, request: ANRequest<*>, code: Int): ANError {
        var error = error
        error = request.parseNetworkError(error)
        error.errorCode = code
        error.errorDetail = ANConstants.RESPONSE_FROM_SERVER_ERROR
        return error
    }

    @JvmStatic
    fun getErrorForParse(error: ANError): ANError {
        error.errorCode = 0
        error.errorDetail = ANConstants.PARSE_ERROR
        return error
    }
}