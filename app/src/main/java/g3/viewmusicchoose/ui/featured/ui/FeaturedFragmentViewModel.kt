package g3.viewmusicchoose.ui.featured.ui

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewmusicchoose.Music
import g3.viewmusicchoose.repo.featured.CheckInternetConnectionUseCase
import g3.viewmusicchoose.repo.featured.GetRemoteAudioDataUseCase
import g3.viewmusicchoose.ui.featured.model.Album
import g3.viewmusicchoose.ui.featured.model.DownloadAudioFromFirebaseUseCase
import g3.viewmusicchoose.util.applyScheduler
import g3.viewmusicchoose.util.notifyObserver
import timber.log.Timber
import javax.inject.Inject

class FeaturedFragmentViewModel @Inject constructor(
    private val checkInternetConnectionUseCase: CheckInternetConnectionUseCase,
    private val getRemoteAudioDataUseCase: GetRemoteAudioDataUseCase,
    private val downloadAudioFromFirebaseUseCase: DownloadAudioFromFirebaseUseCase
) : ViewModel() {

    var hotMusicList = MutableLiveData<MutableList<Music>>()
    var hotAlbumList = MutableLiveData<MutableList<Album>>()
    var strJson = ""

    init {
        hotMusicList.value = ArrayList()
        hotAlbumList.value = ArrayList()
    }

    @SuppressLint("CheckResult")
    fun requestAllFirebaseData() {
        downloadAudioFromFirebaseUseCase.requestHotMusic(strJson).applyScheduler().subscribe({
            Timber.d("congnm request all firebase data ${it.size}")
            hotMusicList?.value?.addAll(it)
            hotMusicList.notifyObserver()
        }, {

        })
    }

    @SuppressLint("CheckResult")
    fun requestStringConfig() {
        downloadAudioFromFirebaseUseCase.requestJsonStr().applyScheduler().subscribe({
            Timber.d("congnm requestStr $it")
            strJson = it
            requestAllFirebaseData()
            requestHotAlbum()
        }, {

        })
    }

    @SuppressLint("CheckResult")
    fun requestHotAlbum() {
        downloadAudioFromFirebaseUseCase.requestHotAlbum(strJson).applyScheduler().subscribe({
            Timber.d("congnm requestAlbum vm $it")
            hotAlbumList.value?.addAll(it)
            hotAlbumList.notifyObserver()
        },{

        })
    }
}