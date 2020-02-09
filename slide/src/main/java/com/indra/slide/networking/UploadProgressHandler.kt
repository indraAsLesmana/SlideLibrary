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

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.indra.slide.common.ANConstants
import com.indra.slide.interfaces.UploadProgressListener
import com.indra.slide.model.Progress

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */
class UploadProgressHandler(private val mUploadProgressListener: UploadProgressListener?) :
    Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message) {
        when (msg.what) {
            ANConstants.UPDATE -> if (mUploadProgressListener != null) {
                val progress = msg.obj as Progress
                mUploadProgressListener.onProgress(progress.currentBytes, progress.totalBytes)
            }
            else -> super.handleMessage(msg)
        }
    }

}