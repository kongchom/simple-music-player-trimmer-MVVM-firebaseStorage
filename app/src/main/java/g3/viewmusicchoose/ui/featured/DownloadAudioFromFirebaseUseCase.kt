package g3.viewmusicchoose.ui.featured

import g3.viewmusicchoose.Music
import g3.viewmusicchoose.repo.featured.IMusicRepository
import io.reactivex.Completable
import io.reactivex.Single
import lib.managerstorage.ManagerStorage
import javax.inject.Inject

class DownloadAudioFromFirebaseUseCase @Inject constructor(
    private val repo: IMusicRepository
){

    fun request(): Single<List<Music>> {
        return repo.getStringConfigJson().flatMap {
            repo.saveFileToLocal(it)
        }
    }

}
