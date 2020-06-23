package g3.viewmusicchoose.ui.featured.model

import com.google.gson.annotations.SerializedName
import g3.viewmusicchoose.Music
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

class Album : RealmObject() {
    @PrimaryKey
    @SerializedName("album_name")
    private val name: String? = null

    @SerializedName("album_total_track")
    private val numOfTrack: Int? = null

    @SerializedName("album_thumb")
    private val thumbPath: String? = null

    @Ignore
    private val listAudio: List<Music>? = null

}