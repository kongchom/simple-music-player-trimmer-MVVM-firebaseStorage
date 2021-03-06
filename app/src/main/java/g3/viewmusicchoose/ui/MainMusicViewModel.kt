package g3.viewmusicchoose.ui

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewmusicchoose.GlobalDef
import g3.viewmusicchoose.Music
import g3.viewmusicchoose.RealmUtil
import g3.viewmusicchoose.SharePrefUtils
import g3.viewmusicchoose.repo.CheckInternetConnectionUseCase
import g3.viewmusicchoose.ui.featured.model.Album
import g3.viewmusicchoose.ui.featured.model.DownloadAudioFromFirebaseUseCase
import g3.viewmusicchoose.util.applyScheduler
import g3.viewmusicchoose.util.notifyObserver
import lib.managerstorage.ManagerStorage
import lib.managerstorage.OnDownloadFileListener
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class MainMusicViewModel @Inject constructor(
    private val checkInternetConnectionUseCase: CheckInternetConnectionUseCase,
    private val downloadAudioFromFirebaseUseCase: DownloadAudioFromFirebaseUseCase
): ViewModel() {

    var numberOfLoad: Int = 0
    var showProgressDialog = MutableLiveData<Boolean>()
    var isShowErrorScreen = MutableLiveData<Boolean>()

    init {
        isShowErrorScreen.value = false
        showProgressDialog.value = false
        numberOfLoad  = SharePrefUtils.getInt(GlobalDef.SHARF_RELOAD_LIST_AUDIO,0)
        Timber.d("congnm numberOfLoad ${numberOfLoad.toString()}")
    }

    /**
     *
     */
    @SuppressLint("CheckResult")
    fun initData() {
        if (numberOfLoad % 5 == 0) {
            //check wifi to download list
            checkInternetConnectionUseCase.request().applyScheduler().subscribe({ isConnected ->
                Timber.d("congnm request wifi ${isConnected.toString()}")
                if (isConnected) {
                    requestStringConfig()
                } else {
                    showErrorScreen(true)
                }
            },{
            })
        } else {
            //case no wifi -> exit app -> turn on wifi -> enter app again
            if (RealmUtil.getInstance().getList(Music::class.java).isNullOrEmpty() || RealmUtil.getInstance().getList(Album::class.java).isNullOrEmpty()) {
                checkInternetConnectionUseCase.request().applyScheduler().subscribe({ isConnected ->
                    Timber.d("congnm request wifi ${isConnected.toString()}")
                    if (isConnected) {
                        requestStringConfig()
                    } else {
                        showErrorScreen(true)
                    }
                },{
                })
            }
        }
    }

    @SuppressLint("CheckResult")
    fun requestStringConfig() {
        showProgressDialog(true)
        downloadAudioFromFirebaseUseCase.requestJsonStr().applyScheduler().subscribe({
            showProgressDialog(false)
            showErrorScreen(false)
        },{
            showErrorScreen(true)
            showProgressDialog(false)
            Timber.d("congnm request json onError ${it.message}")
        })
    }

    @SuppressLint("CheckResult")
    fun downloadCurrentTrack(name: String?,onDone: (Boolean) -> Unit) {
        //Check internet connection
        checkInternetConnectionUseCase.request().applyScheduler().subscribe({ isWifiConnected ->
            //If internet is available, request download file from firebase
            if (isWifiConnected) {
                ManagerStorage.downloadFileToExternalStorage(
                    GlobalDef.PATH_DOWNLOAD_AUDIO + name,
                    GlobalDef.FOLDER_AUDIO,
                    name!!,
                    object: OnDownloadFileListener {
                        override fun OnSuccessListener(file: File) {
                            onDone.invoke(true)
                        }
                        override fun OnFailListener() {
                            onDone.invoke(false)
                        }
                    }
                )
            } else {
                onDone.invoke(false)
            }
        },{
            onDone.invoke(false)
        })
    }

    fun showProgressDialog(show: Boolean) {
        showProgressDialog.value = show
        showProgressDialog.notifyObserver()
    }

    fun showErrorScreen(show: Boolean) {
        isShowErrorScreen.value = show
        isShowErrorScreen.notifyObserver()
    }

    fun updateItemHotMusic(item: Music) {
        RealmUtil.getInstance().realm.executeTransaction { realm ->
            val itemHotMusic = realm.where(Music::class.java).equalTo("name",item.name).findFirst()
            itemHotMusic?.isDownloaded = true
        }
    }

    fun checkFileExist(item: Music): Boolean {
        return File(GlobalDef.FOLDER_AUDIO + item.audioFileName).exists()
    }
}