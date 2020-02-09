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

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import com.indra.slide.networking.InternalRunnable
import java.util.concurrent.*

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */
class ANExecutor internal constructor(
    maxNumThreads: Int,
    threadFactory: ThreadFactory?
) : ThreadPoolExecutor(
    maxNumThreads, maxNumThreads, 0, TimeUnit.MILLISECONDS,
    PriorityBlockingQueue(), threadFactory
) {
    fun adjustThreadCount(info: NetworkInfo?) {
        if (info == null || !info.isConnectedOrConnecting) {
            setThreadCount(DEFAULT_THREAD_COUNT)
            return
        }
        when (info.type) {
            ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_WIMAX, ConnectivityManager.TYPE_ETHERNET -> setThreadCount(
                4
            )
            ConnectivityManager.TYPE_MOBILE -> when (info.subtype) {
                TelephonyManager.NETWORK_TYPE_LTE, TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_EHRPD -> setThreadCount(
                    3
                )
                TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_EVDO_B -> setThreadCount(
                    2
                )
                TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE -> setThreadCount(
                    1
                )
                else -> setThreadCount(DEFAULT_THREAD_COUNT)
            }
            else -> setThreadCount(DEFAULT_THREAD_COUNT)
        }
    }

    private fun setThreadCount(threadCount: Int) {
        corePoolSize = threadCount
        maximumPoolSize = threadCount
    }

    override fun submit(task: Runnable): Future<*> {
        val futureTask =
            AndroidNetworkingFutureTask(task as InternalRunnable)
        execute(futureTask)
        return futureTask
    }

    private class AndroidNetworkingFutureTask(private val hunter: InternalRunnable) :
        FutureTask<InternalRunnable?>(hunter, null),
        Comparable<AndroidNetworkingFutureTask> {
        override fun compareTo(other: AndroidNetworkingFutureTask): Int {
            val p1 = hunter.priority
            val p2 = other.hunter.priority
            return if (p1 === p2) hunter.sequence - other.hunter.sequence else p2.ordinal - p1.ordinal
        }

    }

    companion object {
        private const val DEFAULT_THREAD_COUNT = 3
    }
}