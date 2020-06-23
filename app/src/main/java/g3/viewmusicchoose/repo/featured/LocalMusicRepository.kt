package g3.viewmusicchoose.repo.featured

import android.content.Context
import com.google.gson.Gson
import g3.viewmusicchoose.*
import io.reactivex.Completable
import io.reactivex.Single
import io.realm.Realm
import lib.managerstorage.ManagerStorage
import lib.managerstorage.OnGetStringFromUrlListener
import timber.log.Timber
import java.io.File
import java.io.InputStream
import javax.inject.Inject

class LocalMusicRepository @Inject constructor(
    private val context: Context
) : IMusicRepository {

    override fun getRemoteAudio(): Single<List<LocalSong>> {

        return Single.just(emptyList())
    }

    override fun getAllFirebaseData(): Completable {
        TODO("Not yet implemented")
    }

    override fun getStringConfigJson(): Single<String> {
        val storageReferenceChild =
            ManagerStorage.storage.reference.child(GlobalDef.FB_URL_VIDEO_MAKER_AUDIO)
        val localFile = File.createTempFile("fileTemp", "")
        return Single.create { emitter ->
            storageReferenceChild.getFile(localFile).addOnSuccessListener {
                val inputStream: InputStream = localFile.inputStream()
                val inputString = inputStream.bufferedReader().use { it.readText() }
                inputStream.close()
                localFile.delete()
                emitter.onSuccess(inputString)
            }.addOnFailureListener {
                Timber.d("congnm getStringConfigJson error ${it.message}")
                it.printStackTrace()
                emitter.onError(it)
            }
        }
    }

    override fun saveFileToLocal(str: String): Single<List<Music>> {
        val gSon = Gson()
        val musics: List<Music> =
            gSon.fromJson(str, MusicResponse::class.java).music
        FunctionUtils.createFolder(GlobalDef.FOLDER_AUDIO)
        for (music in musics) {
            // Append with real local path
            val audioPath = GlobalDef.FOLDER_AUDIO + music.audioFileName
            Timber.d("congnm saveFileTolocal $audioPath")
            //                        Lo.d(TAG, "AUDIO_SERVER_NAME = " + music.getName());
//                        Lo.d(TAG, "AUDIO_PATH = " + audioPath);
//                        Lo.d(TAG, "AUDIO_NAME = " + music.getAudioFileName());
            val localFile = File(audioPath)
            // If audio is exist, set flag download to true (don't needed re-download)
            if (localFile.exists()) {
                music.isDownload = true
            }
        }
        // Set to list and Realm
        saveAudioData(musics)
        return Single.just(musics)
    }

    private fun saveAudioData(musics: List<Music>) {
        // Delete old values and insert NEW
        ThreadUtils.getInstance().runBackground(object : ThreadUtils.IBackground {
            override fun doingBackground() {
                val realm = Realm.getDefaultInstance()
                realm.use { realm ->
                    realm.beginTransaction()
                    val eClassTheme =
                        realm.where(Music::class.java).findAll()
                    eClassTheme.deleteAllFromRealm()
                    realm.copyToRealmOrUpdate<Music>(musics)
                    realm.commitTransaction()
                }
            }
            override fun onCompleted() {}
            override fun onCancel() {}
        })
    }
}