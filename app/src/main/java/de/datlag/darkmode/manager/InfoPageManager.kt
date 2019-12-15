package de.datlag.darkmode.manager

import android.animation.Animator
import android.text.Spanned
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import de.datlag.darkmode.R
import de.datlag.darkmode.extend.AdvancedActivity
import io.codetail.animation.ViewAnimationUtils
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.html.HtmlPlugin
import java.util.*
import kotlin.math.hypot
import kotlin.math.max


class InfoPageManager(private val advancedActivity: AdvancedActivity,
                      private val mainLayout: FrameLayout,
                      private val infoLayout: FrameLayout) {

    lateinit var githubIcon: AppCompatImageView
    lateinit var codeIcon: AppCompatImageView
    lateinit var helpIcon: AppCompatImageView
    private var isReverse: Boolean = false

    fun initListener() {
        githubIcon.setOnClickListener {
            advancedActivity.browserIntent(advancedActivity.getString(R.string.github_repo))
        }

        codeIcon.setOnClickListener {
            advancedActivity.applyDialogAnimation(AlertDialog.Builder(advancedActivity)
                .setTitle(advancedActivity.getString(R.string.dependencies))
                .setItems(advancedActivity.resources.getStringArray(R.array.dependencies)) { _, which: Int ->
                    advancedActivity.browserIntent(advancedActivity.resources.getStringArray(R.array.dependencies_link)[which])
                }
                .setPositiveButton(advancedActivity.getString(R.string.okay), null)
                .create()).show()
        }

        helpIcon.setOnClickListener {
            informationDialog()
        }
    }

    fun start(reverse: Boolean = isReverse) {
        if (reverse) {
            infoLayout.visibility = View.GONE
            mainLayout.visibility = View.VISIBLE
            isReverse = false
        } else {
            infoReveal()
            isReverse = true
        }
    }

    private fun infoReveal() {
        val dx =
            max(mainLayout.right, infoLayout.width - mainLayout.right)
        val dy = max(0, infoLayout.height)
        val finalRadius =
            hypot(dx.toDouble(), dy.toDouble()).toFloat()
        val animator =
            ViewAnimationUtils.createCircularReveal(
                infoLayout,
                mainLayout.right,
                0,
                0f,
                finalRadius
            )
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = duration
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                mainLayout.visibility = View.GONE
                infoLayout.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator) {
                mainLayout.visibility = View.VISIBLE
                infoLayout.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
        infoLayout.visibility = View.VISIBLE
        animator.start()
    }

    private fun informationDialog() {
        advancedActivity.applyDialogAnimation(AlertDialog.Builder(advancedActivity)
            .setTitle(advancedActivity.getString(R.string.about))
            .setMessage(advancedActivity.getString(R.string.about_text_part1)+" ${getAge(2001,8,24)} "
            +advancedActivity.getString(R.string.about_text_part2))
            .setPositiveButton(advancedActivity.getString(R.string.close), null)
            .setNeutralButton(advancedActivity.getString(R.string.privacy_policy)) { _, _ ->
                privacyPolicy()
            }
            .create()).show()
    }

    private fun privacyPolicy() {
        val url: String = advancedActivity.getString(R.string.dsgvo_url) + "?viaJS=true"
        val requestQueue: RequestQueue = Volley.newRequestQueue(advancedActivity)

        val markwon = Markwon.builder(advancedActivity)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(HtmlPlugin.create())
            .build()

        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener { response ->
            val text: Spanned = markwon.toMarkdown(response)

            advancedActivity.applyDialogAnimation(AlertDialog.Builder(advancedActivity)
                .setTitle(advancedActivity.getString(R.string.privacy_policy))
                .setMessage(text)
                .setPositiveButton(advancedActivity.getString(R.string.okay), null)
                .setNeutralButton(advancedActivity.getString(R.string.open_in_browser)) { _, _ ->
                    advancedActivity.browserIntent(advancedActivity.getString(R.string.dsgvo_url))
                }
                .create()).show()
        }, Response.ErrorListener {
            Snackbar.make(advancedActivity.findViewById(R.id.coordinator),
                advancedActivity.getString(R.string.privacy_policy_error),
                Snackbar.LENGTH_LONG).show()
        })

        requestQueue.add(stringRequest)
    }

    private fun getAge(year: Int, month: Int, day: Int): Int {
        val calenderToday = Calendar.getInstance()
        val currentYear = calenderToday[Calendar.YEAR]
        val currentMonth = 1 + calenderToday[Calendar.MONTH]
        val todayDay = calenderToday[Calendar.DAY_OF_MONTH]
        var age = currentYear - year

        if (month > currentMonth) {
            --age
        } else if (month == currentMonth) {
            if (day > todayDay) {
                --age
            }
        }
        return age
    }

    companion object {
        private const val duration: Long = 1000
    }
}