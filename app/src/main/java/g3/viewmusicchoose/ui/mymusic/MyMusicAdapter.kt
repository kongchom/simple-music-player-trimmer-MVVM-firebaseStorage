package g3.viewmusicchoose.ui.mymusic

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import g3.viewmusicchoose.LocalSong
import g3.viewmusicchoose.Music
import g3.viewmusicchoose.R
import g3.viewmusicchoose.ui.featured.ui.HotMusicAdapter
import kotlinx.android.synthetic.main.item_hot_music.view.*
import timber.log.Timber

class MyMusicAdapter constructor(private var myMusicList: List<LocalSong>) :
    RecyclerView.Adapter<MyMusicAdapter.MyMusicViewHolder>() {

    var onItemClick: ((LocalSong, Int) -> Unit)? = null
    var lastPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMusicViewHolder =
        when (viewType) {
            TYPE_NORMAL -> MyMusicViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_hot_music, parent, false)
            )
            TYPE_ADS -> MyMusicViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_hot_music_ads, parent, false)
            )
            else -> MyMusicViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_hot_music, parent, false)
            )
        }

    override fun getItemCount(): Int {
        return if (myMusicList.size > 8) {
            myMusicList.size * 2
        } else {
            myMusicList.size
        }
    }

    override fun onBindViewHolder(holder: MyMusicAdapter.MyMusicViewHolder, position: Int) =
        when (holder.itemViewType) {
            TYPE_NORMAL -> holder.bind(myMusicList[position % myMusicList.size])
            else -> holder.bind(myMusicList[position % myMusicList.size])
        }

    override fun getItemViewType(position: Int): Int = TYPE_NORMAL

    inner class MyMusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(myMusicList[absoluteAdapterPosition % myMusicList.size],absoluteAdapterPosition % myMusicList.size)
            }
        }

        @SuppressLint("ResourceAsColor")
        fun bind(item: LocalSong) {
            with(itemView) {
                item_hot_music_duration.text = item.durationText
                item_hot_music_name.text = item.songTitle
                iv_disk.setImageResource(R.drawable.default_music_picture)
                item_hot_music_download.visibility = View.GONE
                if (item.isSelect) {
                    item_hot_music_container.setBackgroundColor(R.color.c_black_alpha_70)
                } else {
                    item_hot_music_container.setBackgroundResource(R.color.c_333333)
                }
            }
        }
    }

    fun setItemSelected(position: Int, isDownloaded: Boolean) {
        Timber.d("congnm set item selected $position - $lastPosition")
        myMusicList[position].isSelect = true
        if (lastPosition != -1 && lastPosition != position) {
            myMusicList[lastPosition].isSelect = false
        }
        lastPosition = position
        notifyDataSetChanged()
    }

    companion object {
        const val TYPE_ADS = 1
        const val TYPE_NORMAL = 0
    }
}