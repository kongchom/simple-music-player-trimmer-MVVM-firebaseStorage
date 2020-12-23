package g3.viewmusicchoose.ui.featured.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import g3.viewmusicchoose.R
import g3.viewmusicchoose.ui.featured.model.Album
import kotlinx.android.synthetic.main.item_hot_album.view.*

class HotAlbumAdapter constructor(var hotAlbumList: List<Album>) :
    RecyclerView.Adapter<HotAlbumAdapter.HotAlbumViewHolder>() {

    var onItemClick: ((Album, Int) -> Unit)? = null
    var onDownloadClick: ((Album, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotAlbumViewHolder =
        HotAlbumViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_hot_album, parent, false)
        )

    override fun getItemCount(): Int = hotAlbumList.size

    override fun onBindViewHolder(holder: HotAlbumViewHolder, position: Int) {
        holder.bind(hotAlbumList[position])
    }

    inner class HotAlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(hotAlbumList[absoluteAdapterPosition], absoluteAdapterPosition)
            }
        }

        fun bind(item: Album) {
            with(itemView) {
                hot_album_name.text = item.getName()
                if (item.getThumb().isEmpty()) {
                    hot_album_thumb.setImageResource(R.drawable.default_album)
                } else {
                    hot_album_thumb.setImageBitmap(item.getThumbBitmap())
                }
                hot_album_number_of_track.text = item.getNumOfTrack().toString()
            }
        }
    }

}
