package de.datlag.darkmode.util

import android.content.*
import android.content.pm.PackageManager
import de.datlag.darkmode.receiver.BootReceiver
import de.datlag.darkmode.receiver.ShutdownReceiver
import java.lang.Exception

class ReceiverUtil {

    companion object {

        private val bootReceiver = BootReceiver()
        private val shutdownReceiver = ShutdownReceiver()

        fun register(context: Context) {
            val bootFilter = IntentFilter(Intent.ACTION_BOOT_COMPLETED)
            bootFilter.addAction(BootReceiver.QUICKBOOT_POWERON)

            val shutdownFilter = IntentFilter(Intent.ACTION_SHUTDOWN)
            shutdownFilter.addAction(ShutdownReceiver.QUICKBOOT_POWEROFF)

            unregister(context)
            context.registerReceiver(bootReceiver, bootFilter)
            context.registerReceiver(shutdownReceiver, shutdownFilter)

            notKillReceiver(context, bootReceiver::class.java)
            notKillReceiver(context, shutdownReceiver::class.java)
        }

        fun unregister(context: Context) {
            try {
                context.unregisterReceiver(bootReceiver)
                context.unregisterReceiver(shutdownReceiver)
            } catch (ignored: Exception) { }

            killReceiver(context, bootReceiver::class.java)
            killReceiver(context, shutdownReceiver::class.java)
        }

        private fun killReceiver(context: Context, clazz: Class<out BroadcastReceiver>) {
            val runningReceiver = ComponentName(context.applicationContext.packageName, clazz.name)
            context.packageManager.setComponentEnabledSetting(
                runningReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }

        private fun notKillReceiver(context: Context, clazz: Class<out BroadcastReceiver>) {
            val newReceiver = ComponentName(context.applicationContext.packageName, clazz.name)
            if (context.packageManager.getComponentEnabledSetting(newReceiver) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
                context.packageManager.setComponentEnabledSetting(
                    newReceiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
        }

    }
}