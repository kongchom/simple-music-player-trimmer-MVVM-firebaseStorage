package g3.viewmusicchoose.ui.featured.model

import g3.viewmusicchoose.repo.featured.LocalMusicRepository
import io.reactivex.Completable
import javax.inject.Inject

class AddAudioToLocalUseCase @Inject constructor(
    private val repo: LocalMusicRepository
){

}
