package g3.viewmusicchoose.ui.featured

import g3.viewmusicchoose.Music
import g3.viewmusicchoose.repo.featured.CheckInternetConnectionUseCase
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class GetAllFirebaseDataUseCase @Inject constructor(
    private val checkWifiConnectionUseCase: CheckInternetConnectionUseCase,
    private val downloadFromFirebaseUseCase: DownloadAudioFromFirebaseUseCase,
    private val addAudioToLocalUseCase: AddAudioToLocalUseCase
) {
    fun request(): Single<List<Music>> {
        return downloadFromFirebaseUseCase
            .request()
    }
}
