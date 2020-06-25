package g3.viewmusicchoose.repo.featured

import g3.viewmusicchoose.LocalSong
import g3.viewmusicchoose.Music
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class GetLocalAudioDataUseCase @Inject constructor(
    private val repo: IMusicRepository
) {
    fun observeData(cbData: (() -> Unit))  {
        return repo.observeData(cbData)
    }
}
