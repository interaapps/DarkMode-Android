package de.datlag.darkmode.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.datlag.darkmode.util.BootUtil

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if(it.action == Intent.ACTION_BOOT_COMPLETED && context != null) {
                BootUtil.applyMode(context)
            }
        }
    }

}