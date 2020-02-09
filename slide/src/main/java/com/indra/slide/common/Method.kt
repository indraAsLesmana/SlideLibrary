package com.indra.slide.common

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */
interface Method {
    companion object {
        const val GET = 0
        const val POST = 1
        const val PUT = 2
        const val DELETE = 3
        const val HEAD = 4
        const val PATCH = 5
        const val OPTIONS = 6
    }
}