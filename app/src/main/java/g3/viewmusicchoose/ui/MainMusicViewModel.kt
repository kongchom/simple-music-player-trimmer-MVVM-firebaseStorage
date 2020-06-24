package g3.viewmusicchoose.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewmusicchoose.GlobalDef
import g3.viewmusicchoose.SharePrefUtils
import g3.viewmusicchoose.repo.featured.CheckInternetConnectionUseCase
import g3.viewmusicchoose.util.applyScheduler
import timber.log.Timber
import javax.inject.Inject

class MainMusicViewModel @Inject constructor(
    private val checkInternetConnectionUseCase: CheckInternetConnectionUseCase,
    private val isRequestFirebaseDataNeededUC: IsRequestDataNeededUseCase
): ViewModel() {

    var numberOfLoad: Int = 0

    init {
        numberOfLoad  = SharePrefUtils.getInt(GlobalDef.SHARF_RELOAD_LIST_AUDIO,0)
    }

    fun initData() {
        if (numberOfLoad % 5 == 0) {
            checkInternetConnectionUseCase.request().applyScheduler().subscribe({
                Timber.d("congnm request wifi ${it.toString()}")
            },{

            })
        }
    }

    override fun onCleared() {
        super.onCleared()
        SharePrefUtils.putInt(GlobalDef.SHARF_RELOAD_LIST_AUDIO,numberOfLoad.plus(1))
    }
}