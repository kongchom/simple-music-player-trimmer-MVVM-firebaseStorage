package g3.viewmusicchoose.ui.featured.model

import g3.viewmusicchoose.Music
import g3.viewmusicchoose.repo.featured.CheckInternetConnectionUseCase
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class GetAllFirebaseDataUseCase @Inject constructor(
    private val checkWifiConnectionUseCase: CheckInternetConnectionUseCase,
    private val downloadFromFirebaseUseCase: DownloadAudioFromFirebaseUseCase,
    private val addAudioToLocalUseCase: AddAudioToLocalUseCase
) {

}
