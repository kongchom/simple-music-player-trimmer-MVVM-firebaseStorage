package g3.viewmusicchoose.ui.featured.ui

import android.annotation.SuppressLint
import android.provider.Settings
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewmusicchoose.GlobalDef
import g3.viewmusicchoose.Music
import g3.viewmusicchoose.RealmUtil
import g3.viewmusicchoose.repo.featured.CheckInternetConnectionUseCase
import g3.viewmusicchoose.repo.featured.GetLocalAudioDataUseCase
import g3.viewmusicchoose.ui.featured.model.Album
import g3.viewmusicchoose.ui.featured.model.DownloadAudioFromFirebaseUseCase
import g3.viewmusicchoose.util.applyScheduler
import g3.viewmusicchoose.util.notifyObserver
import lib.managerstorage.ManagerStorage
import lib.managerstorage.OnDownloadFileListener
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class FeaturedFragmentViewModel @Inject constructor(
    private val getLocalAudioDataUseCase: GetLocalAudioDataUseCase,
    private val checkInternetConnectionUseCase: CheckInternetConnectionUseCase
) : ViewModel() {

    var hotMusicList = MutableLiveData<MutableList<Music>>()
    var hotAlbumList = MutableLiveData<MutableList<Album>>()
    var isPlaying = MutableLiveData<Boolean>()
    var strJson = ""
    val cb = getLocalAudioDataUseCase.observeData() {
        Timber.d("congnm call back observe data")
        initData()
    }

    init {
        isPlaying.value = false
        hotMusicList.value = ArrayList()
        hotAlbumList.value = ArrayList()
    }

    fun initData() {
        getHotAlbum()
        getHotMusic()
    }

    private fun getHotAlbum() {
        hotAlbumList.value?.clear()
        hotAlbumList.value?.addAll(RealmUtil.getInstance().getList(Album::class.java))
        hotAlbumList.notifyObserver()
    }

    private fun getHotMusic() {
        hotMusicList.value?.clear()
        hotMusicList.value?.addAll(RealmUtil.getInstance().getList(Music::class.java))
        hotMusicList.notifyObserver()
    }

    @SuppressLint("CheckResult")
    fun downloadCurrentTrack(name: String?, position: Int, onDone: (Boolean) -> Unit) {
        //Check internet connection
        checkInternetConnectionUseCase.request().applyScheduler().subscribe({
            //If internet is available, request download file from firebase
            val trackPath = GlobalDef.FOLDER_AUDIO + name
            Timber.d(("congnm trackpath $trackPath"))
            ManagerStorage.downloadFileToExternalStorage(
                GlobalDef.PATH_DOWNLOAD_AUDIO + name,
                GlobalDef.FOLDER_AUDIO,
                name!!,
                object: OnDownloadFileListener {
                    override fun OnSuccessListener(file: File) {
                        Timber.d("congnm download success ${file.absolutePath}")
                        hotMusicList.value!![position].isDownloaded = true
                        hotMusicList.notifyObserver()
                        onDone.invoke(true)
                    }
                    override fun OnFailListener() {
                    }
                }
            )
        },{
            onDone.invoke(false)
        })

    }

}