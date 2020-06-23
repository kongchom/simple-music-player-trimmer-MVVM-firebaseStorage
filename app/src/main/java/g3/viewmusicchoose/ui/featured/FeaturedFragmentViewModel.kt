package g3.viewmusicchoose.ui.featured

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewmusicchoose.Music
import g3.viewmusicchoose.repo.featured.CheckInternetConnectionUseCase
import g3.viewmusicchoose.repo.featured.GetRemoteAudioDataUseCase
import g3.viewmusicchoose.ui.featured.model.GetAllFirebaseDataUseCase
import g3.viewmusicchoose.util.applyScheduler
import g3.viewmusicchoose.util.notifyObserver
import timber.log.Timber
import javax.inject.Inject

class FeaturedFragmentViewModel @Inject constructor(
    private val checkInternetConnectionUseCase: CheckInternetConnectionUseCase,
    private val getRemoteAudioDataUseCase: GetRemoteAudioDataUseCase,
    private val getAllFirebaseDataDataUseCase: GetAllFirebaseDataUseCase
) : ViewModel() {

    var hotMusicList = MutableLiveData<MutableList<Music>>()
    var hotAlbumList = MutableLiveData<MutableList<Music>>()
    var strJson = ""

    init {
        hotMusicList.value = ArrayList()
        hotAlbumList.value = ArrayList()
    }

    @SuppressLint("CheckResult")
    fun requestAllFirebaseData() {
        getAllFirebaseDataDataUseCase.request(strJson).applyScheduler().subscribe({
            Timber.d("congnm request all firebase data ${it.size}")
            hotMusicList?.value?.addAll(it)
            hotMusicList.notifyObserver()
        }, {

        })
    }

    fun requestStringConfig() {
        val str = getAllFirebaseDataDataUseCase.requestStr().applyScheduler().subscribe({
            Timber.d("congnm requestStr $it")
            strJson = it
            requestAllFirebaseData()
        }, {

        })
    }
}