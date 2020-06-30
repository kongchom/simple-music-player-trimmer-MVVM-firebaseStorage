package g3.viewmusicchoose.ui.effects

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.annotations.SerializedName
import g3.viewmusicchoose.Music
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.File

open class EffectAlbum: RealmObject() {

    @PrimaryKey
    @SerializedName("effect_name")
    private var name: String = ""

    @SerializedName("effect_total_track")
    private var numOfTrack: Int = 0

    @SerializedName("effect_thumb")
    private var thumbPath: String = ""

    @SerializedName("list_audio")
    private var listAudioOfEffectAlbum: RealmList<Music>? = null

    fun getName() : String {
        return this.name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getThumb() : String {
        return this.thumbPath
    }

    fun getThumbBitmap(): Bitmap {
        return BitmapFactory.decodeFile(File(this.thumbPath).toString())
    }

    fun setThumb(path: String) {
        this.name = thumbPath
    }

    fun getNumOfTrack() : Int {
        return this.numOfTrack
    }

    fun getListAudio(): RealmList<Music> {
        return this.listAudioOfEffectAlbum!!
    }

    fun setListAudio(list: RealmList<Music>) {
        this.listAudioOfEffectAlbum = list
    }
}