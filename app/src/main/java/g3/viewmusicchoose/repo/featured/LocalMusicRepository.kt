package g3.viewmusicchoose.repo.featured

import android.content.Context
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import com.google.gson.Gson
import g3.viewmusicchoose.*
import g3.viewmusicchoose.ui.effects.EffectAlbum
import g3.viewmusicchoose.ui.featured.model.Album
import io.reactivex.Completable
import io.reactivex.Single
import io.realm.Realm
import lib.managerstorage.ManagerStorage
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.lang.Exception
import javax.inject.Inject

class LocalMusicRepository @Inject constructor(
    private val context: Context
) : IMusicRepository {

    var cbData: (() -> Unit)? = null

    override fun getEffectAlbumList(): Single<List<EffectAlbum>> {
        Timber.d("get effect data time ${System.currentTimeMillis()}")
        return Single.just(RealmUtil.getInstance().getList(EffectAlbum::class.java))
    }

    override fun observeData(cb:() -> Unit) {
        cbData = cb
    }

    override fun getStringConfigJson(): Completable {
        Timber.d("congnm getStringConfigJson")
        val storageReferenceChild =
            ManagerStorage.storage.reference.child(GlobalDef.FB_URL_VIDEO_MAKER_AUDIO)
        val localFile = File.createTempFile("fileTemp", "")
        val inputStream: InputStream = localFile.inputStream()
        var inputStr = ""
        return Completable.create { emitter ->
            storageReferenceChild.getFile(localFile).addOnSuccessListener {
                try {
                    inputStr  = inputStream.bufferedReader().use { it.readText() }
                } catch (e: Exception) {
                    emitter.onError(e)
                }
                inputStream.close()
                localFile.delete()
                saveData(inputStr)
               emitter.onComplete()
            }.addOnFailureListener {
                Timber.d("congnm getStringConfigJson error ${it.message}")
                it.printStackTrace()
                emitter.onError(it)
            }
        }
    }

    private fun saveData(str: String) {
        val gSon = Gson()
        val musics: List<Music> = gSon.fromJson(str, MusicResponse::class.java).music
        val hotAlbumList: List<Album> = gSon.fromJson(str, MusicResponse::class.java).album
        val effectAlbumList: List<EffectAlbum> = gSon.fromJson(str, MusicResponse::class.java).effects
        FunctionUtils.createFolder(GlobalDef.FOLDER_AUDIO)
        // Set to list and Realm
        saveEffectAlbum(effectAlbumList)
        Timber.d("save data time ${System.currentTimeMillis()}")
        saveHotAlbum(hotAlbumList)
        saveAudioData(musics)
}

    private fun saveEffectAlbum(effectAlbumList: List<EffectAlbum>) {
        ThreadUtils.getInstance().runBackground(object : ThreadUtils.IBackground {
            override fun doingBackground() {
                val realm = Realm.getDefaultInstance()
                realm.use { realm ->
                    realm.beginTransaction()
                    val eClassTheme =
                        realm.where(EffectAlbum::class.java).findAll()
                    eClassTheme.deleteAllFromRealm()
                    realm.copyToRealmOrUpdate<EffectAlbum>(effectAlbumList)
                    realm.commitTransaction()
                }
            }
            override fun onCompleted() {
                cbData?.invoke()
            }
            override fun onCancel() {}
        })
    }

    override fun isWifiConnected(): Single<Boolean> {
        val wifiMgr = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return Single.just(wifiMgr.connectionInfo.supplicantState == SupplicantState.COMPLETED)
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
                    realm.insertOrUpdate(musics)
                    realm.commitTransaction()
                }
            }
            override fun onCompleted() {
                cbData?.invoke()
            }
            override fun onCancel() {}
        })
    }

    private fun saveHotAlbum(albums: List<Album>) {
        // Delete old values and insert NEW
        ThreadUtils.getInstance().runBackground(object : ThreadUtils.IBackground {
            override fun doingBackground() {
                val realm = Realm.getDefaultInstance()
                realm.use { realm ->
                    realm.beginTransaction()
                    val eClassTheme =
                        realm.where(Album::class.java).findAll()
                    eClassTheme.deleteAllFromRealm()
                    realm.copyToRealmOrUpdate<Album>(albums)
                    realm.commitTransaction()
                }
            }
            override fun onCompleted() {
                cbData?.invoke()
            }
            override fun onCancel() {}
        })
    }
}