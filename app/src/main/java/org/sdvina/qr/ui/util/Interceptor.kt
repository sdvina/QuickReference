package org.sdvina.qr.ui.util

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import org.sdvina.qr.data.constant.LogConstant
import java.io.ByteArrayInputStream

object Interceptor {

    fun dismiss(request: WebResourceRequest?, keywords: String): WebResourceResponse? {
        request?.let {
            val url = request.url.toString()
            if (url.contains(keywords)) {
                Log.d(LogConstant.TAG_INTERCEPTION, "${LogConstant.MSG_DISMISS_URL} $url")
                return WebResourceResponse(
                    "application/json",
                    "utf-8",
                    ByteArrayInputStream(byteArrayOf())
                )
            }
        }
        return null
    }

    fun replace(request: WebResourceRequest?, keywords: String, newResponse: WebResourceResponse): WebResourceResponse? {
        request?.let {
            val url = request.url.toString()
            if (url.contains(keywords)) {
                Log.d(LogConstant.TAG_INTERCEPTION, "${LogConstant.MSG_REPLACE_URL} $url")
                return newResponse
            }
        }
        return null
    }
}