package de.datlag.darkmode.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import de.datlag.darkmode.commons.sharedPreferences
import java.lang.ref.WeakReference

sealed class NightModeHelper {
    class Theme(activity: Activity) {
        private val activityReference = WeakReference(activity)

        init {
            val currentMode = (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)
            if (sUiNightMode == Configuration.UI_MODE_NIGHT_UNDEFINED) {
                sUiNightMode = currentMode
            }
        }

        @Throws(IllegalStateException::class)
        private fun updateConfig(uiNightMode: Int) {
            val activity = activityReference.get() ?: throw IllegalStateException("Activity destroyed")

            val newConfig = Configuration(activity.resources.configuration)
            newConfig.uiMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()
            newConfig.uiMode = newConfig.uiMode or uiNightMode

            activity.onConfigurationChanged(newConfig)
            sUiNightMode = uiNightMode
        }

        fun getConfigMode(): Int = sUiNightMode

        fun changeTo(dark: Boolean) {
            if (dark) {
                updateConfig(Configuration.UI_MODE_NIGHT_YES)
            } else {
                updateConfig(Configuration.UI_MODE_NIGHT_NO)
            }
        }

        companion object {
            private var sUiNightMode = Configuration.UI_MODE_NIGHT_UNDEFINED
        }
    }

    class Util(private val context: Context, activity: Activity? = null) {
        private val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        private var theme: Theme? = if (activity == null) null else Theme(activity)

        fun applyNightMode(mode: NightMode, saveAsync: Boolean = true) {
            uiModeManager.enableCarMode(0)
            var toDark = uiModeManager.nightMode == UiModeManager.MODE_NIGHT_NO

            when(mode) {
                NightMode.LIGHT -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    uiModeManager.nightMode = UiModeManager.MODE_NIGHT_NO
                    toDark = false
                }
                NightMode.DARK -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    uiModeManager.nightMode = UiModeManager.MODE_NIGHT_YES
                    toDark = true
                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    uiModeManager.nightMode = UiModeManager.MODE_NIGHT_AUTO
                }
            }
            saveMode(mode, saveAsync)

            uiModeManager.disableCarMode(0)
            theme?.changeTo(toDark)
        }

        @SuppressLint("ApplySharedPref")
        fun saveMode(mode: NightMode, saveAsync: Boolean = true) {
            context.sharedPreferences.edit().putInt(SHARED_PREF_MODE, mode.type).apply {
                if (saveAsync) apply() else commit()
            }
        }

        fun getMode(): NightMode {
            val delegateMode  = getDelegateMode()
            val uiMode = getUiMode()

            return if (delegateMode == NightMode.LIGHT || uiMode == NightMode.LIGHT) {
                NightMode.LIGHT
            } else if (delegateMode == NightMode.DARK || uiMode == NightMode.DARK) {
                NightMode.DARK
            } else {
                when(context.sharedPreferences.getInt(SHARED_PREF_MODE, NightMode.SYSTEM.type)) {
                    NightMode.LIGHT.type -> NightMode.LIGHT
                    NightMode.DARK.type -> NightMode.DARK
                    else -> NightMode.SYSTEM
                }
            }
        }

        fun getUiMode(): NightMode {
            return if (uiModeManager.nightMode == UiModeManager.MODE_NIGHT_NO || theme?.getConfigMode() == Configuration.UI_MODE_NIGHT_NO) {
                NightMode.LIGHT
            } else if(uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES || theme?.getConfigMode() == Configuration.UI_MODE_NIGHT_YES) {
                NightMode.DARK
            } else {
                NightMode.SYSTEM
            }
        }

        fun getDelegateMode(): NightMode {
            return when(AppCompatDelegate.getDefaultNightMode()) {
                AppCompatDelegate.MODE_NIGHT_NO -> NightMode.LIGHT
                AppCompatDelegate.MODE_NIGHT_YES -> NightMode.DARK
                else -> NightMode.SYSTEM
            }
        }
    }

    enum class NightMode(val type: Int) {
        LIGHT(0),
        DARK(1),
        SYSTEM(2)
    }

    companion object {
        const val SHARED_PREF_MODE: String = "night_mode"
    }
}