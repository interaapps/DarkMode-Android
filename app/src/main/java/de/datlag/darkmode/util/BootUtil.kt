package de.datlag.darkmode.util

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import de.datlag.darkmode.receiver.BootReceiver
import de.datlag.darkmode.receiver.ShutdownReceiver

class BootUtil {
    companion object {

        @SuppressLint("ApplySharedPref")
        @JvmStatic
        fun saveMode(context: Context) {
            val pref = context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
            val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            pref.edit().putInt("darkMode", uiModeManager.nightMode).commit()
        }

        @JvmStatic
        fun getMode(context: Context, recursion: Boolean = false): Int {
            val pref = context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
            var mode = pref.getInt("darkMode", 420)
            if(mode == 420) {
                saveMode(context)
                mode = if(recursion) UiModeManager.MODE_NIGHT_AUTO else getMode(context, true)
            }
            return mode
        }

        @JvmStatic
        fun clearMode(context: Context) {
            val pref = context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
            pref.edit().clear().apply()
        }

        @JvmStatic
        fun applyMode(context: Context) {
            val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            uiModeManager.nightMode = getMode(context)
            clearMode(context)
        }

        @JvmStatic
        fun registerBoot(context: Context) {
            context.registerReceiver(BootReceiver(), IntentFilter(Intent.ACTION_BOOT_COMPLETED))
        }

        @JvmStatic
        fun registerShutdown(context: Context) {
            context.registerReceiver(ShutdownReceiver(), IntentFilter(Intent.ACTION_SHUTDOWN))
        }
    }
}