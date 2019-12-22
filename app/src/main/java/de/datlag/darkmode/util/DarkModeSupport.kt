package de.datlag.darkmode.util

import de.datlag.darkmode.enum.ThemeInt
import de.datlag.darkmode.enum.ThemeName

class DarkModeSupport {
    companion object {
        private val nameList: Array<ThemeName> = ThemeName.values()
        private val intList: Array<ThemeInt> = ThemeInt.values()
        private val nameRegex: Regex = Regex("Theme" +      //Theme Required
                "((.)?(Overlay|MaterialComponents|AppCompat))?" +   //Optional Overlay etc
                ".(\\S+)." +                                        //App name (NOT SAFE) Required
                "(Black|Dark|DayNight|Night)")                      //Black, Dark etc Required

        fun isSupported(theme: Int, themeName: String): Boolean {
            var support = false
            intList.forEach {
                if(theme == it.themeInt)
                    support = true
            }

            if (!support)
                nameList.forEach {
                    if(themeName.contains(it.themeName))
                        support = true
                }

            if(!support)
                support = nameRegex.containsMatchIn(themeName)

            return support
        }
    }
}