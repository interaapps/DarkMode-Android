package de.datlag.darkmode

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.FrameLayout
import android.widget.RadioGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import de.datlag.darkmode.extend.AdvancedActivity
import de.datlag.darkmode.manager.InfoPageManager

class MainActivity : AdvancedActivity() {

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val mainLayout: FrameLayout by bindView(R.id.main)
    private val infoLayout: FrameLayout by bindView(R.id.info_layout)
    private val radioGroup: RadioGroup by bindView(R.id.radio_group)
    private val infoIcon: AppCompatImageView by bindView(R.id.info_icon)
    private val closeIcon: AppCompatImageView by bindView(R.id.close_icon)
    private val githubIcon: AppCompatImageView by bindView(R.id.github_icon)
    private val codeIcon: AppCompatImageView by bindView(R.id.code_icon)
    private val helpIcon: AppCompatImageView by bindView(R.id.help_icon)

    private lateinit var uiModeManager: UiModeManager
    private lateinit var contextThemeWrapper: ContextThemeWrapper
    private lateinit var infoPageManager: InfoPageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
            infoIcon.setColorFilter(Color.WHITE)
        } else {
            infoIcon.setColorFilter(Color.BLACK)
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
                contextThemeWrapper.setTheme(R.style.LightTheme)
                recreate()
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                contextThemeWrapper.setTheme(R.style.DarkTheme)
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
