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
import com.indra.slide.interfaces.UploadProgressListener
import com.indra.slide.model.Progress
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */
class RequestProgressBody(
    private val requestBody: RequestBody,
    uploadProgressListener: UploadProgressListener?
) : RequestBody() {
    private var bufferedSink: BufferedSink? = null
    private var uploadProgressHandler: UploadProgressHandler? = null
    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(sink(sink))
        }
        requestBody.writeTo(bufferedSink)
        bufferedSink!!.flush()
    }

    private fun sink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            var bytesWritten = 0L
            var contentLength = 0L
            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (contentLength == 0L) {
                    contentLength = contentLength()
                }
                bytesWritten += byteCount
                uploadProgressHandler?.obtainMessage(
                    ANConstants.UPDATE,
                    Progress(bytesWritten, contentLength)
                )?.sendToTarget()
            }
        }
    }

    init {
        if (uploadProgressListener != null) {
            uploadProgressHandler = UploadProgressHandler(uploadProgressListener)
        }
    }
}