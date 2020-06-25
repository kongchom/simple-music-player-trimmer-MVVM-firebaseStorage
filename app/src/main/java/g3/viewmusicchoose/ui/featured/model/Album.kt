package g3.viewmusicchoose.ui.featured.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.annotations.SerializedName
import g3.viewmusicchoose.Music
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.File

open class Album : RealmObject() {

    @PrimaryKey
    @SerializedName("album_name")
    private var name: String = ""

    @SerializedName("album_total_track")
    private var numOfTrack: Int = 0

    @SerializedName("album_thumb")
    private var thumbPath: String = ""

    @SerializedName("list_audio")
    private var listAudioOfHotAlbum: RealmList<Music>? = null

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
        return this.listAudioOfHotAlbum!!
    }

    fun setListAudio(list: RealmList<Music>) {
        this.listAudioOfHotAlbum = list
    }
}