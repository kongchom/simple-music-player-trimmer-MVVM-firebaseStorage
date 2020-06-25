package g3.viewmusicchoose.repo.featured

import android.content.Context
import g3.viewmusicchoose.LocalSong
import g3.viewmusicchoose.Music
import g3.viewmusicchoose.ui.featured.model.Album
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

interface IMusicRepository {
    fun getRemoteAudio(): Single<List<LocalSong>>

    fun getAllFirebaseData(): Completable

    fun getStringConfigJson(): Completable

    fun isWifiConnected(): Single<Boolean>

    fun observeData(cbData: (() -> Unit))
}