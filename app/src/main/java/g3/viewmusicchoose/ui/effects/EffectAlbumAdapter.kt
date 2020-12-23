package g3.viewmusicchoose.ui.effects

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import g3.viewmusicchoose.R
import kotlinx.android.synthetic.main.item_effect_album.view.*

class EffectAlbumAdapter constructor(var effectAlbumList: List<EffectAlbum>) :
    RecyclerView.Adapter<EffectAlbumAdapter.EffectAlbumViewHolder>() {

    var onItemClick: ((EffectAlbum, Int) -> Unit)? = null
    var onDownloadClick: ((EffectAlbum, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EffectAlbumViewHolder =
        EffectAlbumViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_effect_album, parent, false)
        )

    override fun getItemCount(): Int = effectAlbumList.size

    override fun onBindViewHolder(holder: EffectAlbumViewHolder, position: Int) {
        holder.bind(effectAlbumList[position])
    }

    inner class EffectAlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(
                    effectAlbumList[absoluteAdapterPosition],
                    absoluteAdapterPosition
                )
            }
        }

        fun bind(item: EffectAlbum) {
            with(itemView) {
                effect_album_name.text = item.getName()
                if (item.getThumb().isEmpty()) {
                    effect_album_thumb.setImageResource(R.drawable.default_album)
                } else {
                    effect_album_thumb.setImageBitmap(item.getThumbBitmap())
                }
                effect_album_number_of_track.text = item.getNumOfTrack().toString()
            }
        }
    }

    fun updateDataList(effectAlbumList: List<EffectAlbum>) {

    }
}