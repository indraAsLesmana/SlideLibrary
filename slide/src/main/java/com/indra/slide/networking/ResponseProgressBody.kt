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

import com.indra.slide.common.ANConstants
import com.indra.slide.interfaces.DownloadProgressListener
import com.indra.slide.model.Progress
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */
class ResponseProgressBody(
    private val mResponseBody: ResponseBody,
    downloadProgressListener: DownloadProgressListener?
) : ResponseBody() {
    private var bufferedSource: BufferedSource? = null
    private var downloadProgressHandler: DownloadProgressHandler? = null
    override fun contentType(): MediaType? {
        return mResponseBody.contentType()
    }

    override fun contentLength(): Long {
        return mResponseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(mResponseBody.source()))
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalBytesRead: Long = 0
            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                downloadProgressHandler?.obtainMessage(
                    ANConstants.UPDATE,
                    Progress(totalBytesRead, mResponseBody.contentLength())
                )?.sendToTarget()
                return bytesRead
            }
        }
    }

    init {
        if (downloadProgressListener != null) {
            downloadProgressHandler = DownloadProgressHandler(downloadProgressListener)
        }
    }
}