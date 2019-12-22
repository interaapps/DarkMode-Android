package de.datlag.darkmode.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.datlag.darkmode.R
import de.datlag.darkmode.extend.AdvancedActivity
import de.datlag.darkmode.manager.AppSupportInfo
import java.util.*

open class AsyncAppFetcher internal constructor(private val activity: AdvancedActivity):
    AsyncTask<Void, Void, List<AppSupportInfo>>() {

    private lateinit var dialog: AlertDialog
    private val appInfoCollection: MutableList<AppSupportInfo> = ArrayList()

    override fun doInBackground(vararg params: Void?): List<AppSupportInfo> {
        getInstalledApps().forEach {
            appInfoCollection.add(AppSupportInfo(activity, it))
        }
        dialog.cancel()
        return appInfoCollection
    }

    override fun onPreExecute() {
        super.onPreExecute()
        val progressBar = ProgressBar(activity, null, android.R.attr.progressBarStyleLarge)
        progressBar.isIndeterminate = true

        dialog = activity.applyDialogAnimation(MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.app_fetching)
            .setView(progressBar)
            .setCancelable(false)
            .create())
        dialog.show()
    }

    private fun getInstalledApps(): List<ApplicationInfo> {
        val packageManager = activity.packageManager
        val apps: MutableList<ApplicationInfo> = packageManager.getInstalledApplications(
            PackageManager.GET_META_DATA)
        Collections.sort(apps, ApplicationInfo.DisplayNameComparator(packageManager))
        val iterator = apps.iterator()
        while (iterator.hasNext()) {
            val nextItem = iterator.next()
            if (nextItem.flags == ApplicationInfo.FLAG_SYSTEM) {
                iterator.remove()
            }

            try {
                if (packageManager.getLaunchIntentForPackage(nextItem.packageName) == null) {
                    iterator.remove()
                }
            } catch (ignored: Exception){}
        }
        return apps
    }
}