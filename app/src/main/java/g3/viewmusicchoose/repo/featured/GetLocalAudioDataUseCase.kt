package g3.viewmusicchoose.repo.featured

import g3.viewmusicchoose.ui.effects.EffectAlbum
import io.reactivex.Single
import javax.inject.Inject

class GetLocalAudioDataUseCase @Inject constructor(
    private val repo: IMusicRepository
) {
    fun observeData(cbData: (() -> Unit))  {
        return repo.observeData(cbData)
    }

    fun getListEffectAlbum(): Single<List<EffectAlbum>> {
        return repo.getEffectAlbumList()
    }
}
