package de.datlag.darkmode.commons

import android.content.Context
import androidx.fragment.app.Fragment

val Fragment.saveContext: Context
    get() {
        return this.context ?: this.activity ?: requireContext()
    }