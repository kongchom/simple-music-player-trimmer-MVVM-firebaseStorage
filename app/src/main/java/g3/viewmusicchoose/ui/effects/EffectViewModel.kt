package g3.viewmusicchoose.ui.effects

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewmusicchoose.RealmUtil
import g3.viewmusicchoose.ui.featured.model.DownloadAudioFromFirebaseUseCase
import g3.viewmusicchoose.util.notifyObserver
import javax.inject.Inject

class EffectViewModel @Inject constructor(
    downloadAudioFromFirebaseUseCase: DownloadAudioFromFirebaseUseCase
) : ViewModel() {

    var effectAlbumList = MutableLiveData<MutableList<EffectAlbum>>()
    var cb = downloadAudioFromFirebaseUseCase.observeData {
        initData()
    }

    init {
        effectAlbumList.value = ArrayList()
        initData()
    }

    fun initData() {
        getEffectAlbumList()
    }

    @SuppressLint("CheckResult")
    private fun getEffectAlbumList() {
        effectAlbumList.value?.clear()
        effectAlbumList.value?.addAll(RealmUtil.getInstance().getList(EffectAlbum::class.java))
        effectAlbumList.notifyObserver()
    }
}