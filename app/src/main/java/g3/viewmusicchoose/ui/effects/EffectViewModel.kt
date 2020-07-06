package g3.viewmusicchoose.ui.effects

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewmusicchoose.Music
import g3.viewmusicchoose.RealmUtil
import g3.viewmusicchoose.repo.featured.GetLocalAudioDataUseCase
import g3.viewmusicchoose.ui.featured.model.Album
import g3.viewmusicchoose.ui.featured.model.DownloadAudioFromFirebaseUseCase
import g3.viewmusicchoose.util.applyScheduler
import g3.viewmusicchoose.util.notifyObserver
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class EffectViewModel @Inject constructor(
    private val downloadAudioFromFirebaseUseCase: DownloadAudioFromFirebaseUseCase
) : ViewModel() {

    var effectAlbumList = MutableLiveData<MutableList<EffectAlbum>>()

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