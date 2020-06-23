package g3.viewmusicchoose.ui.featured

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewmusicchoose.LocalSong
import g3.viewmusicchoose.repo.featured.CheckInternetConnectionUseCase
import g3.viewmusicchoose.repo.featured.GetRemoteAudioDataUseCase
import g3.viewmusicchoose.util.applyScheduler
import g3.viewmusicchoose.util.notifyObserver
import io.reactivex.Completable
import javax.inject.Inject

class FeaturedFragmentViewModel @Inject constructor(
    private val checkInternetConnectionUseCase: CheckInternetConnectionUseCase,
    private val getRemoteAudioDataUseCase: GetRemoteAudioDataUseCase,
    private val getAllFirebaseDataDataUseCase: GetAllFirebaseDataUseCase
) : ViewModel() {

     var hotMusicList = MutableLiveData<MutableList<LocalSong>>()

    init {
        requestAllFirebaseData()
    }

    @SuppressLint("CheckResult")
    fun requestRemoteData() {
        getRemoteAudioDataUseCase.requestFirebase().applyScheduler().subscribe({
            hotMusicList.value?.addAll(it)
            hotMusicList.notifyObserver()
        }, {
            it.printStackTrace()
        })
    }

    fun requestAllFirebaseData() {
        getAllFirebaseDataDataUseCase.request()
    }
}