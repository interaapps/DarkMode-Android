package de.datlag.darkmode.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.datlag.darkmode.util.AppSupportInfo

class AppsSupportViewModel : ViewModel() {

    private var appsSupportListLive: MutableLiveData<MutableList<AppSupportInfo>> = MutableLiveData()

    fun addItem(item: AppSupportInfo) {
        val list = getList().toMutableList()
        list.add(item)
        appsSupportListLive.postValue(list)
    }

    fun setList(list: List<AppSupportInfo>) {
        appsSupportListLive.value = null
        appsSupportListLive.value = list.toMutableList()
    }

    fun getList(): List<AppSupportInfo> {
        return appsSupportListLive.value?.toList() ?: listOf()
    }
}