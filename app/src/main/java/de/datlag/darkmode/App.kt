package de.datlag.darkmode

import android.app.Application
import de.datlag.darkmode.util.BootUtil
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ViewPump.init(ViewPump.builder()
            .addInterceptor(CalligraphyInterceptor(
                CalligraphyConfig.Builder()
                    .setDefaultFontPath(this.getString(R.string.font_path))
                    .setFontAttrId(R.attr.fontPath)
                    .build()))
            .build())

        BootUtil.registerBoot(this@App)
        BootUtil.registerShutdown(this@App)
    }
}