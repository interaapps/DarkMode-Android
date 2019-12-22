package de.datlag.darkmode

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.os.LocaleListCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.datlag.darkmode.extend.AdvancedActivity
import de.datlag.darkmode.manager.AppSupportInfo
import io.noties.markwon.Markwon
import io.noties.markwon.core.CorePlugin
import kotlinx.android.synthetic.main.fragment_supported_apps_dialog.*
import kotlinx.android.synthetic.main.fragment_supported_apps_dialog_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class SupportedAppsDialogFragment : BottomSheetDialogFragment() {

    private lateinit var activity: AdvancedActivity
    private lateinit var appInfoCollection: List<AppSupportInfo>
    private lateinit var markwon: Markwon
    private lateinit var packageText: Spanned
    private lateinit var versionText: Spanned
    private lateinit var codeText: Spanned
    private lateinit var updateText: Spanned

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supported_apps_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = GridLayoutManager(context, 2)
        list.adapter = SupportedAppsAdapter(appInfoCollection)

        markwon = Markwon.builder(activity).usePlugin(CorePlugin.create()).build()
        packageText = markwon.toMarkdown("**${activity.getString(R.string.app_package)}** ")
        versionText = markwon.toMarkdown("**${activity.getString(R.string.app_version)}** ")
        codeText = markwon.toMarkdown("**${activity.getString(R.string.app_code)}** ")
        updateText = markwon.toMarkdown("**${activity.getString(R.string.app_update)}** ")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    private inner class ViewHolder internal constructor(
        inflater: LayoutInflater,
        parent: ViewGroup
    ) : RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.fragment_supported_apps_dialog_item,
            parent,
            false
        )
    ) {

        internal val appIcon: AppCompatImageView = itemView.app_icon
        internal val appName: AppCompatTextView = itemView.app_name
        internal val appSupport: AppCompatTextView = itemView.app_support
        internal val appCard: CardView = itemView.app_card
    }

    private inner class SupportedAppsAdapter internal constructor(private val appInfoCollection: List<AppSupportInfo>) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val appInfo = appInfoCollection[position]

            holder.appName.text = appInfo.name
            holder.appIcon.setImageDrawable(appInfo.icon)
            if(appInfo.supported)
                holder.appSupport.text = activity.getString(R.string.app_supported)
            else
                holder.appSupport.text = activity.getString(R.string.app_not_supported)


            holder.appCard.setOnClickListener {
                this@SupportedAppsDialogFragment.dismiss()
                val text: Spanned = SpannableStringBuilder("${activity.getString(R.string.app_starting)}\n\n")
                    .append(packageText).append(" ${appInfo.packageName} \n")
                    .append(versionText).append(" ${appInfo.versionName} \n")
                    .append(codeText).append(" ${appInfo.versionCode} \n")
                    .append(updateText).append(" ${convertLongToTime(appInfo.updated)}")

                activity.applyDialogAnimation(MaterialAlertDialogBuilder(activity)
                    .setTitle(appInfo.name)
                    .setMessage(text)
                    .setPositiveButton(R.string.start) { _, _ ->
                        activity.appIntent(appInfo.packageName)
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .create()).show()
            }
        }

        override fun getItemCount(): Int {
            return appInfoCollection.size
        }
    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat(activity.getString(R.string.app_update_pattern), LocaleListCompat.getDefault()[0])
        return format.format(date)
    }

    companion object {
        fun newInstance(activity: AdvancedActivity, appInfoCollection: List<AppSupportInfo>): SupportedAppsDialogFragment {
            val supportedAppsDialogFragment = SupportedAppsDialogFragment()
            supportedAppsDialogFragment.activity = activity
            supportedAppsDialogFragment.appInfoCollection = appInfoCollection
            return supportedAppsDialogFragment
        }
    }
}
