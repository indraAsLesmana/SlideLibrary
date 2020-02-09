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
package com.indra.slide.networking

import android.content.Context
import android.net.TrafficStats
import com.indra.slide.common.ANConstants
import com.indra.slide.common.ANRequest
import com.indra.slide.common.Method
import com.indra.slide.error.ANError
import com.indra.slide.utils.Utils.getCache
import com.indra.slide.utils.Utils.saveFile
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */
object InternalNetworking {
    var sHttpClient = client
    var sUserAgent: String? = null
    @JvmStatic
    @Throws(ANError::class)
    fun performSimpleRequest(request: ANRequest<*>): Response {
        val okHttpRequest: Request
        val okHttpResponse: Response
        try {
            var builder = Request.Builder().url(request.url)
            addHeadersToRequestBuilder(builder, request)
            var requestBody: RequestBody? = null
            when (request.method) {
                Method.GET -> {
                    builder = builder.get()
                }
                Method.POST -> {
                    requestBody = request.requestBody
                    builder = builder.post(requestBody)
                }
                Method.PUT -> {
                    requestBody = request.requestBody
                    builder = builder.put(requestBody)
                }
                Method.DELETE -> {
                    requestBody = request.requestBody
                    builder = builder.delete(requestBody)
                }
                Method.HEAD -> {
                    builder = builder.head()
                }
                Method.OPTIONS -> {
                    builder = builder.method(ANConstants.OPTIONS, null)
                }
                Method.PATCH -> {
                    requestBody = request.requestBody
                    builder = builder.patch(requestBody)
                }
            }
            if (request.cacheControl != null) {
                builder.cacheControl(request.cacheControl)
            }
            okHttpRequest = builder.build()
            if (request.okHttpClient != null) {
                request.setCall(
                    request.okHttpClient.newBuilder().cache(sHttpClient!!.cache()).build().newCall(
                        okHttpRequest
                    )
                )
            } else {
                request.setCall(sHttpClient!!.newCall(okHttpRequest))
            }
            val startTime = System.currentTimeMillis()
            val startBytes = TrafficStats.getTotalRxBytes()
            okHttpResponse = request.call.execute()
            val timeTaken = System.currentTimeMillis() - startTime
            if (okHttpResponse.cacheResponse() == null) {
                val finalBytes = TrafficStats.getTotalRxBytes()
                val diffBytes: Long
                diffBytes =
                    if (startBytes == TrafficStats.UNSUPPORTED.toLong() || finalBytes == TrafficStats.UNSUPPORTED.toLong()) {
                        okHttpResponse.body()!!.contentLength()
                    } else {
                        finalBytes - startBytes
                    }
            }
        } catch (ioe: IOException) {
            throw ANError(ioe)
        }
        return okHttpResponse
    }

    @JvmStatic
    @Throws(ANError::class)
    fun performDownloadRequest(request: ANRequest<*>): Response {
        val okHttpRequest: Request
        val okHttpResponse: Response
        try {
            var builder = Request.Builder().url(request.url)
            addHeadersToRequestBuilder(builder, request)
            builder = builder.get()
            if (request.cacheControl != null) {
                builder.cacheControl(request.cacheControl)
            }
            okHttpRequest = builder.build()
            val okHttpClient: OkHttpClient
            okHttpClient = if (request.okHttpClient != null) {
                request.okHttpClient.newBuilder()
                    .cache(sHttpClient!!.cache())
                    .addNetworkInterceptor { chain ->
                        val originalResponse = chain.proceed(chain.request())
                        originalResponse.newBuilder()
                            .body(
                                ResponseProgressBody(
                                    originalResponse.body()!!,
                                    request.downloadProgressListener
                                )
                            )
                            .build()
                    }.build()
            } else {
                sHttpClient!!.newBuilder()
                    .addNetworkInterceptor { chain ->
                        val originalResponse = chain.proceed(chain.request())
                        originalResponse.newBuilder()
                            .body(
                                ResponseProgressBody(
                                    originalResponse.body()!!,
                                    request.downloadProgressListener
                                )
                            )
                            .build()
                    }.build()
            }
            request.call = okHttpClient.newCall(okHttpRequest)
            val startTime = System.currentTimeMillis()
            val startBytes = TrafficStats.getTotalRxBytes()
            okHttpResponse = request.call.execute()
            saveFile(
                okHttpResponse,
                request.dirPath,
                request.fileName
            )
            val timeTaken = System.currentTimeMillis() - startTime
            if (okHttpResponse.cacheResponse() == null) {
                val finalBytes = TrafficStats.getTotalRxBytes()
                val diffBytes: Long
                diffBytes =
                    if (startBytes == TrafficStats.UNSUPPORTED.toLong() || finalBytes == TrafficStats.UNSUPPORTED.toLong()) {
                        okHttpResponse.body()!!.contentLength()
                    } else {
                        finalBytes - startBytes
                    }
            }
        } catch (ioe: IOException) {
            try {
                val destinationFile =
                    File(request.dirPath + File.separator + request.fileName)
                if (destinationFile.exists()) {
                    destinationFile.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            throw ANError(ioe)
        }
        return okHttpResponse
    }

    @JvmStatic
    var client: OkHttpClient?
        get() = if (sHttpClient == null) {
            defaultClient
        } else sHttpClient
        set(okHttpClient) {
            sHttpClient = okHttpClient
        }

    fun addHeadersToRequestBuilder(
        builder: Request.Builder,
        request: ANRequest<*>
    ) {
        if (request.userAgent != null) {
            builder.addHeader(ANConstants.USER_AGENT, request.userAgent)
        } else if (sUserAgent != null) {
            request.userAgent = sUserAgent
            builder.addHeader(ANConstants.USER_AGENT, sUserAgent)
        }
        val requestHeaders = request.headers
        if (requestHeaders != null) {
            builder.headers(requestHeaders)
            if (request.userAgent != null && !requestHeaders.names().contains(ANConstants.USER_AGENT)) {
                builder.addHeader(ANConstants.USER_AGENT, request.userAgent)
            }
        }
    }

    val defaultClient: OkHttpClient
        get() = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

    @JvmStatic
    fun setClientWithCache(context: Context?) {
        sHttpClient = OkHttpClient().newBuilder()
            .cache(
                getCache(
                    context!!,
                    ANConstants.MAX_CACHE_SIZE,
                    ANConstants.CACHE_DIR_NAME
                )
            )
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @JvmStatic
    fun setUserAgent(userAgent: String?) {
        sUserAgent = userAgent
    }

}