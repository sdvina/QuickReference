package org.sdvina.qr.data.local

import android.content.Context
import android.content.SharedPreferences
import org.sdvina.qr.data.constant.ReferenceConstant

object AppPreferences {
    private lateinit var prefs: SharedPreferences
    private const val NAME = "AppPreferences"

    private const val LAST_VIEWED_URL = "last_viewed_url"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    private inline fun SharedPreferences.edit(
        performEdit: (SharedPreferences.Editor) -> Unit
    ) {
        val editor = this.edit()
        performEdit(editor)
        editor.apply()
    }

    var lastViewedUrl: String?
        get() = prefs.getString(LAST_VIEWED_URL, ReferenceConstant.QR_EN)
        set(value) = prefs.edit { it.putString(LAST_VIEWED_URL, value) }
}