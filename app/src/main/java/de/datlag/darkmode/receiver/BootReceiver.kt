package de.datlag.darkmode.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.datlag.darkmode.helper.NightModeHelper.Util

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if (it.action == Intent.ACTION_BOOT_COMPLETED || it.action == QUICKBOOT_POWERON) {
                context?.let { con ->
                    val nightModeUtil = Util(con)
                    nightModeUtil.applyNightMode(nightModeUtil.getMode())
                }
            }
        }
    }

    companion object {
        const val QUICKBOOT_POWERON: String = "android.intent.action.QUICKBOOT_POWERON"
    }

}