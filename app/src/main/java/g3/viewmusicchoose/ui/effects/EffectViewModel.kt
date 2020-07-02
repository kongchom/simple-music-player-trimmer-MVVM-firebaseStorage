package g3.viewmusicchoose.ui.effects

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewmusicchoose.Music
import g3.viewmusicchoose.RealmUtil
import g3.viewmusicchoose.repo.featured.GetLocalAudioDataUseCase
import g3.viewmusicchoose.ui.featured.model.Album
import g3.viewmusicchoose.util.applyScheduler
import g3.viewmusicchoose.util.notifyObserver
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class EffectViewModel @Inject constructor(
    private val getLocalAudioDataUseCase: GetLocalAudioDataUseCase
) : ViewModel() {

    var effectAlbumList = MutableLiveData<MutableList<EffectAlbum>>()
    var activityTitle = MutableLiveData<String>()
    var needToggleBackButton: Boolean = false

    init {
        Timber.d("init effect viewmodel")
        activityTitle.value = ""
        effectAlbumList.value = ArrayList()
        initData()
    }

     fun initData() {
        getEffectAlbumList()
    }

    @SuppressLint("CheckResult")
    private fun getEffectAlbumList() {
        getLocalAudioDataUseCase.getListEffectAlbum().applyScheduler().subscribe({
            Timber.d("get effect viewmodel data time ${System.currentTimeMillis()}")
            effectAlbumList.value?.clear()
            effectAlbumList.value?.addAll(it)
            effectAlbumList.notifyObserver()
        },{

        })

    }

    fun getStubList(): List<EffectAlbum> {
        val effectAlbum = EffectAlbum()
        effectAlbum.setName("Stub Album")
        return arrayListOf<EffectAlbum>(
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum,
            effectAlbum
        )
    }
}