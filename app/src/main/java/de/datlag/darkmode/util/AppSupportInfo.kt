package de.datlag.darkmode.util

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.pm.PackageInfoCompat

class AppSupportInfo(
    context: Context,
    applicationInfo: ApplicationInfo
) {
    var name: String
    var icon: Drawable
    var supported = false
    var packageName: String
    var versionName: String
    var versionCode: Long
    var updated: Long
    private var packageInfo: PackageInfo
    private var activityInfos: Array<ActivityInfo>
    private var resources: Resources

    init {
        packageInfo = context.packageManager.getPackageInfo(applicationInfo.packageName, PackageManager.GET_ACTIVITIES)
        activityInfos = packageInfo.activities
        packageName = packageInfo.packageName
        name = applicationInfo.loadLabel(context.packageManager).toString()
        icon = applicationInfo.loadIcon(context.packageManager)
        resources = context.packageManager.getResourcesForApplication(applicationInfo)
        supported = isSupported()
        versionName = packageInfo.versionName
        versionCode = PackageInfoCompat.getLongVersionCode(packageInfo)
        updated = packageInfo.lastUpdateTime
    }

    private fun isSupported(): Boolean {
        var support = false
        try {
            activityInfos.forEach {
                val appTheme: Int = it.themeResource
                if (DarkModeSupport.isSupported(appTheme, resources.getResourceEntryName(appTheme)))
                    support = true
            }
        } catch (ignored: Exception){}
        return support
    }
}