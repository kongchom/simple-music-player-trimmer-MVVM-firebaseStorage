package g3.viewmusicchoose.ui.featured.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import g3.viewmusicchoose.Music
import g3.viewmusicchoose.R
import g3.viewmusicchoose.R.drawable.d_core_border_music_name_selected
import kotlinx.android.synthetic.main.item_hot_music.view.*
import timber.log.Timber


class HotMusicAdapter constructor(private var hotMusicList: List<Music>) :
    RecyclerView.Adapter<HotMusicAdapter.HotMusicViewHolder>() {

    var onItemClick: ((Music, Int) -> Unit)? = null
    var lastPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotMusicViewHolder =
        when (viewType) {
            TYPE_NORMAL -> HotMusicViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_hot_music, parent, false)
            )
            TYPE_ADS -> HotMusicViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_hot_music_ads, parent, false)
            )
            else -> HotMusicViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_hot_music, parent, false)
            )
        }

    override fun getItemCount(): Int = hotMusicList.size

    override fun onBindViewHolder(holder: HotMusicViewHolder, position: Int) =
        when (holder.itemViewType) {
            TYPE_NORMAL -> holder.bind(hotMusicList[position])
            else -> holder.bindItemAds(hotMusicList[position])
        }

    override fun getItemViewType(position: Int): Int =
        when (position) {
            1 -> TYPE_ADS
            else -> TYPE_NORMAL
        }

    inner class HotMusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(hotMusicList[absoluteAdapterPosition],absoluteAdapterPosition)
            }
        }

        fun bind(item: Music) {
            with(itemView) {
                item_hot_music_duration.text = item.durationText
                item_hot_music_name.text = item.name
                iv_disk.setImageResource(R.drawable.default_music_picture)
                item_hot_music_download.visibility = if (!item.isSelected || item.isDownloaded) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                if (item.isSelected) {
                    item_hot_music_container.setBackgroundColor(d_core_border_music_name_selected)
                } else {
                    item_hot_music_container.setBackgroundResource(R.color.c_333333)
                }
            }
        }

        fun bindItemAds(item: Music) {

        }
    }

    fun setItemSelected(position: Int) {
        Timber.d("congnm set item selected $position - $lastPosition")
        hotMusicList[position].isSelected = true
        if (lastPosition != -1 && lastPosition != position) {
            hotMusicList[lastPosition].isSelected = false
        }
        lastPosition = position
        notifyDataSetChanged()
    }

    companion object {
        const val TYPE_ADS = 1
        const val TYPE_NORMAL = 0
    }
}