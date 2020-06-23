package g3.viewmusicchoose.ui.featured.model

import g3.viewmusicchoose.Music
import g3.viewmusicchoose.repo.featured.IMusicRepository
import io.reactivex.Completable
import io.reactivex.Single
import lib.managerstorage.ManagerStorage
import timber.log.Timber
import javax.inject.Inject

class DownloadAudioFromFirebaseUseCase @Inject constructor(
    private val repo: IMusicRepository
){

    fun request(str: String): Single<List<Music>> {
        return repo.saveFileToLocal(str)
    }

    fun requestStr() : Single<String> {
        return repo.getStringConfigJson()
    }

}
