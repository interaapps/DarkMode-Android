package de.datlag.darkmode.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.datlag.darkmode.helper.NightModeHelper.Util

class ShutdownReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if (it.action == Intent.ACTION_SHUTDOWN || it.action == QUICKBOOT_POWEROFF) {
                context?.let { con ->
                    val nightModeUtil = Util(con)
                    nightModeUtil.saveMode(nightModeUtil.getMode(), false)
                }
            }
        }
    }

    companion object {
        const val QUICKBOOT_POWEROFF: String = "android.intent.action.QUICKBOOT_POWEROFF"
    }

}