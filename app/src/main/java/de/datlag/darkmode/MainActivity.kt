package de.datlag.darkmode

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.FrameLayout
import android.widget.RadioGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.datlag.darkmode.extend.AdvancedActivity
import de.datlag.darkmode.manager.AppSupportInfo
import de.datlag.darkmode.manager.InfoPageManager
import de.datlag.darkmode.util.AsyncAppFetcher

class MainActivity : AdvancedActivity() {

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val mainLayout: FrameLayout by bindView(R.id.main)
    private val infoLayout: FrameLayout by bindView(R.id.info_layout)
    private val backGround: AppCompatImageView by bindView(R.id.background)
    private val radioGroup: RadioGroup by bindView(R.id.radio_group)
    private val infoIcon: AppCompatImageView by bindView(R.id.info_icon)
    private val closeIcon: AppCompatImageView by bindView(R.id.close_icon)
    private val githubIcon: AppCompatImageView by bindView(R.id.github_icon)
    private val codeIcon: AppCompatImageView by bindView(R.id.code_icon)
    private val helpIcon: AppCompatImageView by bindView(R.id.help_icon)
    private val supportButton: FloatingActionButton by bindView(R.id.support_button)
    private val adView: AdView by bindView(R.id.bottom_ad)

    private lateinit var uiModeManager: UiModeManager
    private lateinit var contextThemeWrapper: ContextThemeWrapper
    private lateinit var infoPageManager: InfoPageManager
    private lateinit var adRequest: AdRequest

    private var appInfoCollection: List<AppSupportInfo> = ArrayList()
    private lateinit var asyncAppFetcher: AsyncAppFetcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this@MainActivity)
        adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        setSupportActionBar(toolbar)
        uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        contextThemeWrapper = ContextThemeWrapper(this@MainActivity, this.theme)
        infoPageManager = InfoPageManager(this@MainActivity, mainLayout, infoLayout)
        infoPageManager.githubIcon = githubIcon
        infoPageManager.codeIcon = codeIcon
        infoPageManager.helpIcon = helpIcon
        infoPageManager.initListener()

        if (savedInstanceState == null) {
            applyRadioButton()
        }

        if (uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES) {
            backGround.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_app_bg_dark))
        } else {
            backGround.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_app_bg_light))
        }

        asyncAppFetcher = object: AsyncAppFetcher(this@MainActivity) {
            override fun onPostExecute(result: List<AppSupportInfo>) {
                super.onPostExecute(result)
                appInfoCollection = result
                appBottomSheet()
            }
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.light_mode -> uiModeManager.nightMode = UiModeManager.MODE_NIGHT_NO
                R.id.dark_mode -> uiModeManager.nightMode = UiModeManager.MODE_NIGHT_YES
                else -> uiModeManager.nightMode = UiModeManager.MODE_NIGHT_AUTO
            }
        }

        infoIcon.setOnClickListener {
            infoPageManager.start()
        }

        closeIcon.setOnClickListener {
            infoPageManager.start()
        }

        supportButton.setOnClickListener {
            if (appInfoCollection.isEmpty()) {
                asyncAppFetcher.execute()
            } else {
                appBottomSheet()
            }
        }
    }

    private fun appBottomSheet() {
        val supportedAppsDialogFragment = SupportedAppsDialogFragment.newInstance(
            this@MainActivity, appInfoCollection)
        supportedAppsDialogFragment.show(supportFragmentManager, supportedAppsDialogFragment.tag)
    }

    private fun applyRadioButton() {
        when(uiModeManager.nightMode) {
            UiModeManager.MODE_NIGHT_AUTO -> radioGroup.check(R.id.system_mode)
            UiModeManager.MODE_NIGHT_YES -> radioGroup.check(R.id.dark_mode)
            UiModeManager.MODE_NIGHT_NO -> radioGroup.check(R.id.light_mode)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                recreate()
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                recreate()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString(STATE_CREATION, STATE_RECREATED)
    }

    companion object {
        private const val STATE_CREATION: String = "STATE_CREATION"
        private const val STATE_RECREATED: String = "STATE_RECREATED"
    }
}
