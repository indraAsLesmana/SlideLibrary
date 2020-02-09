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
package com.indra.slide.core

import android.os.Process
import java.util.concurrent.Executor
import java.util.concurrent.ThreadFactory

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */
class DefaultExecutorSupplier : ExecutorSupplier {
    private val mNetworkExecutor: ANExecutor
    private val mImmediateNetworkExecutor: ANExecutor
    private val mMainThreadExecutor: Executor
    override fun forNetworkTasks(): ANExecutor {
        return mNetworkExecutor
    }

    override fun forImmediateNetworkTasks(): ANExecutor {
        return mImmediateNetworkExecutor
    }

    override fun forMainThreadTasks(): Executor {
        return mMainThreadExecutor
    }

    companion object {
        val DEFAULT_MAX_NUM_THREADS =
            2 * Runtime.getRuntime().availableProcessors() + 1
    }

    init {
        val backgroundPriorityThreadFactory: ThreadFactory =
            PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND)
        mNetworkExecutor = ANExecutor(
            DEFAULT_MAX_NUM_THREADS,
            backgroundPriorityThreadFactory
        )
        mImmediateNetworkExecutor = ANExecutor(2, backgroundPriorityThreadFactory)
        mMainThreadExecutor = MainThreadExecutor()
    }
}