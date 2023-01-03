package com.igrocery.overpriced.shared

import android.util.Log

/**
 * A simple logger that proxies the [android.util.Log].
 *
 * Automatically uses the file name as the log tag.
 */
class Logger(emptyFunc: () -> Unit) {

    private val logTag = resolveClassName(emptyFunc)

    private fun resolveClassName(emptyFunc: () -> Unit): String {
        val name = emptyFunc.javaClass.name
        val slicedName = when {
            name.contains("Kt$") -> name.substringAfterLast(".").substringBefore("Kt$")
            name.contains("$") -> name.substringAfterLast(".").substringBefore("$")
            else -> name
        }
        return slicedName
    }

    fun verbose(message: String) = Log.v(logTag, message)

    fun debug(message: String) = Log.d(logTag, message)

    fun info(message: String) = Log.i(logTag, message)

    fun warn(message: String) = Log.w(logTag, message)

    fun error(message: String) = Log.e(logTag, message)

    fun wtf(message: String) = Log.wtf(logTag, message)

}
