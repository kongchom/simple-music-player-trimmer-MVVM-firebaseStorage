package g3.viewmusicchoose.ui.featured.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import g3.viewmusicchoose.Music
import g3.viewmusicchoose.R
import kotlinx.android.synthetic.main.item_hot_music.view.*
import timber.log.Timber

class HotMusicAdapter constructor(private var hotMusicList: List<Music>, private var isContainAds: Boolean) :
    RecyclerView.Adapter<HotMusicAdapter.HotMusicViewHolder>() {

    var onItemClick: ((Music, Int) -> Unit)? = null
    var onDownloadClick: ((Music, Int) -> Unit)? = null
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

    override fun getItemCount(): Int = if (hotMusicList.size > 7) {
        hotMusicList.size * 2
    } else {
        hotMusicList.size
    }

    override fun onBindViewHolder(holder: HotMusicViewHolder, position: Int) =
        when (holder.itemViewType) {
            TYPE_NORMAL -> {
//                Timber.d("congnm onbindviewholder position $position")
                if (position >= 2 && isContainAds && position % hotMusicList.size - 1 >= 0) {
                    holder.bind(hotMusicList[position % hotMusicList.size - 1])
                } else {
                    holder.bind(hotMusicList[position % hotMusicList.size])
                }
            }
            else -> holder.bindItemAds()
        }

    override fun getItemViewType(position: Int): Int =
        when (position % hotMusicList.size) {
            1 -> if (isContainAds) TYPE_ADS else TYPE_NORMAL
            else -> TYPE_NORMAL
        }

    inner class HotMusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                if (absoluteAdapterPosition >= 2 && isContainAds && absoluteAdapterPosition % hotMusicList.size - 1 >= 0 ) {
                    onItemClick?.invoke(hotMusicList[absoluteAdapterPosition % hotMusicList.size - 1],absoluteAdapterPosition % hotMusicList.size - 1)
                } else if (absoluteAdapterPosition == 1 && isContainAds) {
                    return@setOnClickListener
                } else {
                    onItemClick?.invoke(hotMusicList[absoluteAdapterPosition % hotMusicList.size],absoluteAdapterPosition % hotMusicList.size)
                }
            }
        }

        @SuppressLint("ResourceAsColor")
        fun bind(item: Music) {
            with(itemView) {
//                    Timber.d("congnm absolute adapter position $absoluteAdapterPosition")
                item_hot_music_duration.text = item.durationText
                item_hot_music_name.text = item.name
                iv_disk.setImageResource(R.drawable.default_music_picture)
                item_hot_music_download.visibility = if (item.isDownloaded) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                item_hot_music_download.setOnClickListener {
                    onDownloadClick?.invoke(item, absoluteAdapterPosition)
                }
                if (item.isSelected) {
                    item_hot_music_container.setBackgroundColor(R.color.c_black_alpha_70)
                } else {
                    item_hot_music_container.setBackgroundResource(R.color.c_333333)
                }
            }
        }

        fun bindItemAds() {

        }
    }

    fun setItemSelected(position: Int) {
        Timber.d("congnm set item selected $position - $lastPosition")
        //Un select all items by set position = -1
        if (position == -1){
            hotMusicList[lastPosition].isSelected = false
        }
        hotMusicList[position].isSelected = true
        if (lastPosition != -1 && lastPosition != position) {
            hotMusicList[lastPosition].isSelected = false
        }
        lastPosition = position
        notifyDataSetChanged()
    }

    fun setItemDownloaded(position: Int) {
        hotMusicList[position].isDownloaded = true
        notifyDataSetChanged()
    }

    companion object {
        const val TYPE_ADS = 1
        const val TYPE_NORMAL = 0
    }
}