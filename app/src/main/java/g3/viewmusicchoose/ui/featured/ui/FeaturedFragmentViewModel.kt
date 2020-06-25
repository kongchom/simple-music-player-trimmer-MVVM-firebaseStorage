package g3.viewmusicchoose.ui.featured.ui

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewmusicchoose.Music
import g3.viewmusicchoose.RealmUtil
import g3.viewmusicchoose.repo.featured.CheckInternetConnectionUseCase
import g3.viewmusicchoose.repo.featured.GetLocalAudioDataUseCase
import g3.viewmusicchoose.ui.featured.model.Album
import g3.viewmusicchoose.ui.featured.model.DownloadAudioFromFirebaseUseCase
import g3.viewmusicchoose.util.applyScheduler
import g3.viewmusicchoose.util.notifyObserver
import timber.log.Timber
import javax.inject.Inject

class FeaturedFragmentViewModel @Inject constructor(
    private val getLocalAudioDataUseCase: GetLocalAudioDataUseCase
) : ViewModel() {

    var hotMusicList = MutableLiveData<MutableList<Music>>()
    var hotAlbumList = MutableLiveData<MutableList<Album>>()
    var strJson = ""
    val cb = getLocalAudioDataUseCase.observeData() {
        Timber.d("congnm call back observe data")
        initData()
    }

    init {
        hotMusicList.value = ArrayList()
        hotAlbumList.value = ArrayList()
    }

    fun initData() {
        getHotAlbum()
        getHotMusic()
    }

    private fun getHotAlbum() {
        hotAlbumList.value?.addAll(RealmUtil.getInstance().getList(Album::class.java))
        hotAlbumList.notifyObserver()
    }

    private fun getHotMusic() {
        hotMusicList.value?.addAll(RealmUtil.getInstance().getList(Music::class.java))
        hotMusicList.notifyObserver()
    }
}