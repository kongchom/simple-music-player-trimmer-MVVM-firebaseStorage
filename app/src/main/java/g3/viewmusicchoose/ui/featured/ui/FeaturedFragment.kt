package g3.viewmusicchoose.ui.featured.ui

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import g3.viewmusicchoose.*
import kotlinx.android.synthetic.main.activity_main_music.*
import kotlinx.android.synthetic.main.fragment_featured.*
import org.w3c.dom.Text
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class FeaturedFragment : Fragment() {

    lateinit var mViewModel: FeaturedFragmentViewModel
    @Inject set

    private lateinit var hotMusicRv: RecyclerView
    private lateinit var hotMusicAdapter: HotMusicAdapter
    lateinit var playMusicView: View
    lateinit var btnDownload: View
    lateinit var playMusicButton: ImageView
    lateinit var playMusicTrackTitle: TextView
    lateinit var playMusicTrackDuration: TextView

    lateinit var mediaPlayer: MediaPlayer
    @Inject set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_featured,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MusicApplication.instance.appComponent.inject(this)
        initView()
        observeData()
    }

    private fun observeData() {
        mViewModel.initData()
        mViewModel.hotMusicList.observe(requireActivity(), Observer {
            Timber.d("congnm onObserve hot music ${it.size}")
            hotMusicAdapter = HotMusicAdapter(it)
            hotMusicRv.adapter = hotMusicAdapter
            hotMusicAdapter.notifyDataSetChanged()
            hotMusicAdapter.onItemClick = { item, position ->
                initPlayMusicView(item)
                if (item.isDownloaded && position != hotMusicAdapter.lastPosition) {
                    hotMusicAdapter.setItemSelected(position)
                    //init play music view + play music
                    val myUri: Uri = Uri.fromFile(File(GlobalDef.FOLDER_AUDIO + item.audioFileName))
                    playMusic(myUri)
                }
            }
            hotMusicAdapter.onDownloadClick = { item, position ->
                mViewModel.downloadCurrentTrack(item.audioFileName,position) {
                    hotMusicAdapter.setDownloadedItem(position)
                    //init play music view + play music
                    val myUri: Uri = Uri.fromFile(File(GlobalDef.FOLDER_AUDIO + item.audioFileName))
                    playMusic(myUri)
                }
            }
            hotMusicRv.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
        })
        mViewModel.hotAlbumList.observe(requireActivity(), Observer {
            featured_fragment_hot_album_rv.adapter = HotAlbumAdapter(it)
            featured_fragment_hot_album_rv.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)
        })
    }

    private fun initView() {
        hotMusicRv = requireView().findViewById(R.id.featured_fragment_hot_music_rv)
        playMusicView = activity?.findViewById(R.id.music_activity_play_music_container)!!
        playMusicButton = activity?.findViewById(R.id.play_music_button)!!
        playMusicTrackTitle = activity?.findViewById(R.id.play_music_track_title)!!
        playMusicTrackDuration = activity?.findViewById(R.id.play_music_track_duration)!!
    }

    private fun playMusic(uri: Uri) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.release()
        }
        mediaPlayer = MediaPlayer.create(requireContext(),uri)
        mediaPlayer.start()
        playMusicButton.setImageResource(R.drawable.icon_pause)
        Timber.d("congnm play music")
        playMusicButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                playMusicButton.setImageResource(R.drawable.icon_play_music)
                Timber.d("congnm observe play pause")
            } else {
                mediaPlayer.start()
                playMusicButton.setImageResource(R.drawable.icon_pause)
            }
        }
    }

    private fun initPlayMusicView(item: Music) {
        playMusicView.visibility = View.VISIBLE
        playMusicTrackTitle.text = item.name
        playMusicTrackDuration.text = item.durationText
    }

    override fun onDestroyView() {
        Timber.d("congnm ondestroy view featured fragment")
        mediaPlayer.release()
        playMusicView.visibility = View.GONE
        super.onDestroyView()
    }
}
