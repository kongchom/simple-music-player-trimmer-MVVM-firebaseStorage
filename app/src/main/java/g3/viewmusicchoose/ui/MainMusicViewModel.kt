package g3.viewmusicchoose.ui

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewmusicchoose.GlobalDef
import g3.viewmusicchoose.Music
import g3.viewmusicchoose.RealmUtil
import g3.viewmusicchoose.SharePrefUtils
import g3.viewmusicchoose.repo.featured.CheckInternetConnectionUseCase
import g3.viewmusicchoose.ui.featured.model.Album
import g3.viewmusicchoose.ui.featured.model.DownloadAudioFromFirebaseUseCase
import g3.viewmusicchoose.util.applyScheduler
import g3.viewmusicchoose.util.notifyObserver
import timber.log.Timber
import javax.inject.Inject

class MainMusicViewModel @Inject constructor(
    private val checkInternetConnectionUseCase: CheckInternetConnectionUseCase,
    private val downloadAudioFromFirebaseUseCase: DownloadAudioFromFirebaseUseCase
): ViewModel() {

    var numberOfLoad: Int = 0
    var hotMusicList = MutableLiveData<MutableList<Music>>()
    var hotAlbumList = MutableLiveData<MutableList<Album>>()
    var isShowErrorScreen = MutableLiveData<Boolean>()

    init {
        isShowErrorScreen.value = false
        numberOfLoad  = SharePrefUtils.getInt(GlobalDef.SHARF_RELOAD_LIST_AUDIO,0)
        Timber.d("congnm numberOfLoad ${numberOfLoad.toString()}")
        hotMusicList.value = ArrayList()
        hotAlbumList.value = ArrayList()
    }

    /**
     *
     */
    @SuppressLint("CheckResult")
    fun initData() {
        if (numberOfLoad % 5 == 0) {
            checkInternetConnectionUseCase.request().applyScheduler().subscribe({ isConnected ->
                Timber.d("congnm request wifi ${isConnected.toString()}")
                if (isConnected) {
                    isShowErrorScreen.value = false
                    isShowErrorScreen.notifyObserver()
                    requestStringConfig()
                } else {
                    isShowErrorScreen.value = true
                    isShowErrorScreen.notifyObserver()
                }
            },{
                Timber.d("congnm request wifi fail ${it.toString()}")
            })
        }
    }

    @SuppressLint("CheckResult")
    fun requestStringConfig() {
        downloadAudioFromFirebaseUseCase.requestJsonStr().applyScheduler().subscribe({

        },{
            Timber.d("congnm request json onError ${it.message}")
        })
    }
}