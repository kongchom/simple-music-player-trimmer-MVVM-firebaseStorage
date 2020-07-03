package g3.viewmusicchoose.ui.featured.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewmusicchoose.Music
import g3.viewmusicchoose.RealmUtil
import g3.viewmusicchoose.ui.featured.model.Album
import g3.viewmusicchoose.util.notifyObserver
import javax.inject.Inject

class FeaturedFragmentViewModel @Inject constructor(

) : ViewModel() {

    var hotMusicList = MutableLiveData<MutableList<Music>>()
    var hotAlbumList = MutableLiveData<MutableList<Album>>()

    init {
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
}