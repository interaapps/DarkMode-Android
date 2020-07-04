package de.datlag.darkmode

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import de.datlag.darkmode.bottomsheets.AppsInfoSheet
import de.datlag.darkmode.commons.activity
import de.datlag.darkmode.commons.isInstantApp
import de.datlag.darkmode.commons.showInstall
import de.datlag.darkmode.extend.DarkModeActivity
import de.datlag.darkmode.model.AppsSupportViewModel
import de.datlag.darkmode.util.AppSupportInfo
import de.datlag.darkmode.helper.NightModeHelper.Util
import de.datlag.darkmode.helper.NightModeHelper.NightMode
import de.datlag.darkmode.util.ReceiverUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.*

class MainActivity : DarkModeActivity() {

    private lateinit var adRequest: AdRequest

    private val appsInfoSheet = AppsInfoSheet()
    private lateinit var appsInfoViewModel: AppsSupportViewModel

    private lateinit var contextThemeWrapper: ContextThemeWrapper
    private lateinit var util: Util
    private var animatable2Compat: Animatable2Compat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(activity) {
            adRequest = AdRequest.Builder().build()
            //adView.loadAd(adRequest)
        }

        util = Util(activity)
        contextThemeWrapper = ContextThemeWrapper(activity, this.theme)
        appsInfoViewModel = ViewModelProvider(this).get(AppsSupportViewModel::class.java)

        applyViewChanges()
        applyRadioButton()
        initRadioButtonClick()

        floatingActionButton.setOnClickListener {
            appsInfoSheet.show(supportFragmentManager, appsInfoSheet.tag)
            if (appsInfoViewModel.getList().isNotEmpty()) {
                appsInfoSheet.appsLoaded(appsInfoViewModel)
            } else {
                loadAppsAsync()
            }
        }

        if (isInstantApp) {
            floatingInstallButton.visibility = View.VISIBLE
            floatingInstallButton.setOnClickListener {
                showInstall()
            }
        } else {
            floatingInstallButton.visibility = View.GONE
        }
    }

    private fun applyRadioButton() {
        val mode = util.getMode()
        util.applyNightMode(mode)

        radioGroup.setOnCheckedChangeListener(null)
        when(mode) {
            NightMode.LIGHT -> radioGroup.check(R.id.lightMode)
            NightMode.DARK -> radioGroup.check(R.id.darkMode)
            else -> radioGroup.check(R.id.systemMode)
        }
    }

    private fun initRadioButtonClick() {
        radioGroup.setOnCheckedChangeListener(null)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.lightMode -> {
                    util.applyNightMode(NightMode.LIGHT, false)
                }
                R.id.darkMode -> {
                    util.applyNightMode(NightMode.DARK, false)
                }
                R.id.systemMode -> {
                    util.applyNightMode(NightMode.SYSTEM, false)
                }
            }
        }
    }

    private fun applyViewChanges() {
        val animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(
            activity,
            if (util.getMode() == NightMode.DARK) R.drawable.animated_bg_dark else R.drawable.animated_bg_light
        )
        background.setImageDrawable(animatedVectorDrawableCompat)
        animatable2Compat = background.drawable as Animatable2Compat
    }

    private fun loadAppsAsync() {
        GlobalScope.launch(Dispatchers.IO) {
            val apps: MutableList<ApplicationInfo> = packageManager.getInstalledApplications(
                PackageManager.GET_META_DATA)

            Collections.sort(apps, ApplicationInfo.DisplayNameComparator(packageManager))

            val iterator = apps.iterator()
            while(iterator.hasNext()) {
                val nextItem = iterator.next()
                if (nextItem.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                    iterator.remove()
                    continue
                } else if (nextItem.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0) {
                    iterator.remove()
                    continue
                }

                try {
                    if (packageManager.getLaunchIntentForPackage(nextItem.packageName) == null) {
                        iterator.remove()
                        continue
                    }
                } catch (ignored: Exception) { }

                appsInfoViewModel.addItem(AppSupportInfo(activity, nextItem))
            }

            withContext(Dispatchers.Main) {
                if (activity.isInstantApp && appsInfoViewModel.getList().isEmpty()) {
                    activity.showInstall()
                    appsInfoSheet.instantNoAppsLoaded()
                } else {
                    appsInfoSheet.appsLoaded(appsInfoViewModel)
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        animatable2Compat?.let {
            if(!it.isRunning) {
                it.registerAnimationCallback(object: Animatable2Compat.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        super.onAnimationEnd(drawable)
                        recreate()
                    }
                })

                it.start()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        ReceiverUtil.register(this)
        overridePendingTransition(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
    }

    override fun onDestroy() {
        super.onDestroy()
        ReceiverUtil.unregister(this)
    }
}
