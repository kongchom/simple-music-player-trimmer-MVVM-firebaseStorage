package g3.viewmusicchoose.ui.featured.model

import g3.viewmusicchoose.Music
import g3.viewmusicchoose.repo.featured.IMusicRepository
import io.reactivex.Single
import javax.inject.Inject

class DownloadAudioFromFirebaseUseCase @Inject constructor(
    private val repo: IMusicRepository
){

    fun requestHotMusic(str: String): Single<List<Music>> {
        return repo.getHotMusicList(str)
    }

    fun requestJsonStr() : Single<String> {
        return repo.getStringConfigJson()
    }

    fun requestHotAlbum(str: String): Single<List<Album>> {
        return repo.getHostAlbumList(str)
    }

}
