package g3.viewmusicchoose.ui.effects

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewmusicchoose.RealmUtil
import g3.viewmusicchoose.repo.featured.GetLocalAudioDataUseCase
import g3.viewmusicchoose.ui.featured.model.Album
import g3.viewmusicchoose.util.notifyObserver
import timber.log.Timber
import javax.inject.Inject

class EffectViewModel @Inject constructor(
    private val getLocalAudioDataUseCase: GetLocalAudioDataUseCase
) : ViewModel() {

    var hotAlbumList = MutableLiveData<MutableList<Album>>()

    init {
        hotAlbumList.value = ArrayList()
    }

    val cb = getLocalAudioDataUseCase.observeData {
        Timber.d("congnm call back observe data")
        initData()
    }

    fun initData() {
        getHotAlbum()
    }

    private fun getHotAlbum() {
        hotAlbumList.value?.addAll(RealmUtil.getInstance().getList(Album::class.java))
        hotAlbumList.notifyObserver()
    }

}