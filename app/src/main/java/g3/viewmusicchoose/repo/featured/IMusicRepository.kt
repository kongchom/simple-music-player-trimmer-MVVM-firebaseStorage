package g3.viewmusicchoose.repo.featured

import android.content.Context
import g3.viewmusicchoose.LocalSong
import g3.viewmusicchoose.Music
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

interface IMusicRepository {
    fun getRemoteAudio(): Single<List<LocalSong>>

    fun getAllFirebaseData(): Completable

    fun getStringConfigJson(): Single<String>

    fun saveFileToLocal(str: String): Single<List<Music>>
}