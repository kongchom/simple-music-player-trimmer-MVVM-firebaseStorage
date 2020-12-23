package g3.viewmusicchoose.repo.featured

import g3.viewmusicchoose.ui.effects.EffectAlbum
import io.reactivex.Completable
import io.reactivex.Single

interface IMusicRepository {
    fun getEffectAlbumList(): Single<List<EffectAlbum>>

    fun getStringConfigJson(): Completable

    fun isWifiConnected(): Single<Boolean>

    fun observeData(cbData: (() -> Unit))

}