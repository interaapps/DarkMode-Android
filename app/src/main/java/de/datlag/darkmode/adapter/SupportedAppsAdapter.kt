package de.datlag.darkmode.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import de.datlag.darkmode.R
import de.datlag.darkmode.util.AppSupportInfo
import kotlinx.android.synthetic.main.supported_apps_item.view.*

class SupportedAppsAdapter(private val context: Context) : RecyclerView.Adapter<SupportedAppsAdapter.ViewHolder>() {

    private val dataList: MutableList<AppSupportInfo> = mutableListOf()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = inflater.inflate(R.layout.supported_apps_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appInfo: AppSupportInfo = dataList[position]

        holder.appIcon.setImageDrawable(appInfo.icon)
        holder.appName.text = appInfo.name
        if (appInfo.supported) {
            holder.appSupport.text = context.getString(R.string.app_supported)
        } else {
            holder.appSupport.text = context.getString(R.string.app_not_supported)
        }
    }

    fun addAll(list: List<AppSupportInfo>) {
        dataList.addAll(list)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val appCard: CardView = itemView.app_card
        internal val appIcon: AppCompatImageView = itemView.app_icon
        internal val appName: AppCompatTextView = itemView.app_name
        internal val appSupport: AppCompatTextView = itemView.app_support

        init {
            appCard.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemClickListener?.onItemClick(v ?: itemView, adapterPosition)
        }

    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}