package de.datlag.darkmode.bottomsheets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import de.datlag.darkmode.R
import de.datlag.darkmode.adapter.SupportedAppsAdapter
import de.datlag.darkmode.commons.getStringFromArrayWithBreak
import de.datlag.darkmode.commons.isTelevision
import de.datlag.darkmode.commons.saveContext
import de.datlag.darkmode.model.AppsSupportViewModel
import kotlinx.android.synthetic.main.app_info_sheet.*
import java.lang.ref.WeakReference

@AndroidEntryPoint
class AppsInfoSheet : BottomSheetDialogFragment(), SupportedAppsAdapter.ItemClickListener {

    private lateinit var adapter: SupportedAppsAdapter
    private var appsSupportViewModelReference: WeakReference<AppsSupportViewModel>? = null

    private var creationStateListener: CreationState? = null
    private var viewCreated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (appsSupportViewModelReference?.get()?.getList()?.isNotEmpty() == true) {
            appsLoaded(appsSupportViewModelReference?.get() as AppsSupportViewModel)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adapter = SupportedAppsAdapter(saveContext)

        if(saveContext.packageManager.isTelevision()) {
            dialog?.setOnShowListener {
                val bottomSheetDialog = it as BottomSheetDialog
                val sheetInternal: View? = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
                sheetInternal?.let { sheet ->
                    BottomSheetBehavior.from(sheet).state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
        return inflater.inflate(R.layout.app_info_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appInfoRecycler?.layoutManager = GridLayoutManager(saveContext, 2)
        adapter.itemClickListener = this
        appInfoRecycler?.adapter = adapter

        loadingBar?.indeterminateDrawable = loadingBar.indeterminateDrawable.mutate().apply {
            colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(saveContext, R.color.progress_bar_color),
                BlendModeCompat.SRC_IN
            )
        }

        viewCreated = true
        creationStateListener?.onViewCreated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewCreated = false
    }

    override fun onItemClick(view: View, position: Int) {
        val list = appsSupportViewModelReference?.get()?.getList() ?: listOf()
        if (position > list.size) {
            return
        }

        val intent = saveContext.packageManager.getLaunchIntentForPackage(list[position].packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun appsLoaded(viewModel: AppsSupportViewModel) {
        appsSupportViewModelReference = WeakReference(viewModel)

        if (viewCreated) {
            displayApps()
        } else {
            creationStateListener = object: CreationState{
                override fun onViewCreated() {
                    displayApps()
                }
            }
        }
    }

    fun instantNoAppsLoaded() {
        loadingBar?.visibility = View.GONE
        compatibilityHintText?.text = saveContext.getStringFromArrayWithBreak(R.array.instant_apps_no_apps_hints)
    }

    private fun displayApps() {
        val list = appsSupportViewModelReference?.get()?.getList() ?: listOf()
        adapter.addAll(list)
        adapter.notifyDataSetChanged()
        loadingBar?.visibility = View.GONE
        appInfoRecycler?.visibility = View.VISIBLE
        if(saveContext.packageManager.isTelevision()) {
            appInfoRecycler?.requestFocus()
        }
    }

    interface CreationState {
        fun onViewCreated()
    }
}