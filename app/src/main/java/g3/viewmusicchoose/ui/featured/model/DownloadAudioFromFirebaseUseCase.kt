package g3.viewmusicchoose.ui.featured.model

import g3.viewmusicchoose.Music
import g3.viewmusicchoose.repo.featured.IMusicRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class DownloadAudioFromFirebaseUseCase @Inject constructor(
    private val repo: IMusicRepository
){
    fun requestJsonStr() : Completable {
        return repo.getStringConfigJson()
    }
}
