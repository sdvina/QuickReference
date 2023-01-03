package org.sdvina.qr

import android.app.Application
import org.sdvina.qr.data.local.AppPreferences

class QRApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
    }
}