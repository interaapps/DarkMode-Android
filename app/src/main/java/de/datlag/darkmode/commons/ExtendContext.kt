package de.datlag.darkmode.commons

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.ArrayRes
import com.google.android.gms.instantapps.InstantApps

const val SHARED_PREFS: String = "shared_prefs"
val LINE_BREAK: String = System.getProperty("line.separator") ?: "\r\n"

val Context.sharedPreferences: SharedPreferences
    get() = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

val Context.isInstantApp: Boolean
    get() = InstantApps.getPackageManagerCompat(this).isInstantApp

fun Context.getStringFromArrayWithBreak(@ArrayRes id: Int): String {
    val array: Array<String> = this.resources.getStringArray(id)
    val stringBuilder = StringBuilder()
    for(i in array.indices) {
        if (i != array.size-1) {
            stringBuilder.append("${array[i]} $LINE_BREAK")
        } else {
            stringBuilder.append(array[i])
        }
    }
    return stringBuilder.toString()
}