package de.datlag.darkmode.commons

import android.app.Activity
import com.google.android.gms.instantapps.InstantApps

fun Activity.showInstall(code: Int = INSTANT_APP_REQUEST_CODE) {
    if (isInstantApp) {
        InstantApps.showInstallPrompt(this, null, code, null)
    }
}

val Activity.activity: Activity
    get() = this

const val INSTANT_APP_REQUEST_CODE: Int = 1337