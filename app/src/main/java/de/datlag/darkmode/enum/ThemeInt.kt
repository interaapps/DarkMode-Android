package de.datlag.darkmode.enum

import com.google.android.material.R

enum class ThemeInt(val themeInt: Int) {
    INT_THEME_DAYNIGHT(R.style.Theme_AppCompat_DayNight),
    INT_THEME_DAYNIGHT_DARK(R.style.Theme_AppCompat_DayNight_DarkActionBar),
    INT_THEME_DAYNIGHT_NONE(R.style.Theme_AppCompat_DayNight_NoActionBar),
    INT_THEME_MATERIAL_DAYNIGHT(R.style.Theme_MaterialComponents_DayNight),
    INT_THEME_MATERIAL_DAYNIGHT_DARK(R.style.Theme_MaterialComponents_DayNight_DarkActionBar),
    INT_THEME_MATERIAL_DAYNIGHT_NONE(R.style.Theme_MaterialComponents_DayNight_NoActionBar),
    INT_THEME_MATERIAL_DAYNIGHT_DARK_BRIDGE(R.style.Theme_MaterialComponents_DayNight_DarkActionBar_Bridge),
    INT_THEME_MATERIAL_DAYNIGHT_NONE_BRIDGE(R.style.Theme_MaterialComponents_DayNight_NoActionBar_Bridge),
    INT_THEME_OVERLAY_DAYNIGHT(R.style.ThemeOverlay_AppCompat_DayNight),
    INT_THEME_OVERLAY_DARK(R.style.ThemeOverlay_AppCompat_Dark),
    INT_THEME_OVERLAY_BASE_DARK(R.style.Base_ThemeOverlay_AppCompat_Dark),
    INT_THEME_OVERLAY_PLATFORM_DARK(R.style.Platform_ThemeOverlay_AppCompat_Dark),
}