package g3.viewmusicchoose.repo.featured

import g3.viewmusicchoose.LocalSong
import io.reactivex.Single
import javax.inject.Inject

class GetRemoteAudioDataUseCase @Inject constructor(
    private val repo: IMusicRepository
) {
    fun requestFirebase() : Single<List<LocalSong>> {
        return repo.getRemoteAudio()
    }
}
