package g3.viewmusicchoose.ui.mymusic

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import g3.viewmusicchoose.*
import g3.viewmusicchoose.ui.MainMusicActivity
import g3.viewmusicchoose.ui.featured.ui.FeaturedFragment
import g3.viewmusicchoose.util.MyMediaPlayer
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class MyMusicFragment : Fragment() {

    lateinit var playMusicView: View
    lateinit var btnDownload: View
    lateinit var playMusicButton: ImageView
    lateinit var playMusicTrackTitle: TextView
    lateinit var activityTitle: TextView
    lateinit var trimView: CustomTrimView
    lateinit var activityBackButton: ImageView
    lateinit var playMusicTrackDuration: TextView
    lateinit var myMysicRv: RecyclerView
    lateinit var myMysicRvAdapter: MyMusicAdapter
    lateinit var rvLayoutManager: LinearLayoutManager
    private lateinit var mAct: MainMusicActivity
    private lateinit var listener: MainMusicActivity.HandleOnActivity
    var handler = Handler()

    lateinit var mediaPlayer: MyMediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_music,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MusicApplication.instance.appComponent.inject(this)
        mediaPlayer = MyMediaPlayer.getInstance(requireContext())
        initViews()
        val listLocalSong = MusicUtils.bindAllSongs(requireContext())
        myMysicRvAdapter = MyMusicAdapter(listLocalSong)
        myMysicRv.adapter = myMysicRvAdapter
        rvLayoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        myMysicRv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstItemVisible = rvLayoutManager.findFirstVisibleItemPosition()
                if (firstItemVisible !=0 && firstItemVisible % listLocalSong.size == 0) {
                    (myMysicRv.layoutManager as LinearLayoutManager).scrollToPosition(0)
                }
            }
        })
        myMysicRv.layoutManager = rvLayoutManager
        initListener()
    }

    private fun initListener() {
        myMysicRvAdapter.onItemClick = { item, position ->
            initPlayMusicView(item)
            playMusic(item)
            if (position != myMysicRvAdapter.lastPosition) {
                myMysicRvAdapter.setItemSelected(position, isDownloaded = true)
                //init play music view + play music
            } else {
                myMysicRvAdapter.setItemSelected(position, isDownloaded = false)
                playMusicButton.setImageResource(R.drawable.icon_play_music)
                mediaPlayer.pauseSound(null)
            }
            trimView.setDuration(item.duration)
            trimView.setOnTrimListener { start, end ->
                Timber.d("congnm on trim listener start $start - end $end")
                mediaPlayer.seekTo(start)
                handler.postDelayed( {
                    mediaPlayer.pauseSound(handler)
                    playMusicButton.setImageResource(R.drawable.icon_play_music)
                }, ((end - start) * 1000).toLong())
            }
            playMusicButton.setOnClickListener {
                if (mediaPlayer.checkNotNull() && mediaPlayer.playingState) {
                    mediaPlayer.pauseSound(null)
                    playMusicButton.setImageResource(R.drawable.icon_play_music)
                    Timber.d("congnm observe play")
                } else {
                    Timber.d("congnm on pause my music fragment")
                    if (!mediaPlayer.checkNotNull()) {
                        //after pause by handler, restart sound
                        initPlayMusicView(item)
                        playMusic(item)
                    } else {
                        mediaPlayer.restartSound()
                    }
                    playMusicButton.setImageResource(R.drawable.icon_pause)
                }
            }
        }
    }

    private fun playMusic(item: LocalSong) {
        mediaPlayer.playSound("",item.songData)
        playMusicButton.setImageResource(R.drawable.icon_pause)
        Timber.d("congnm play music")
    }

    private fun initPlayMusicView(item: LocalSong) {
        playMusicView.visibility = View.VISIBLE
        playMusicTrackTitle.text = item.songTitle
        playMusicTrackDuration.text = item.durationText
    }

    private fun initViews() {
        myMysicRv = requireView().findViewById(R.id.my_music_recycler_view)
        playMusicView = activity?.findViewById(R.id.music_activity_play_music_container)!!
        trimView = activity?.findViewById(R.id.music_activity_trim_view)!!
        playMusicButton = activity?.findViewById(R.id.play_music_button)!!
        playMusicTrackTitle = activity?.findViewById(R.id.play_music_track_title)!!
        playMusicTrackDuration = activity?.findViewById(R.id.play_music_track_duration)!!
        activityTitle = activity?.findViewById(R.id.music_activity_title)!!
        activityBackButton = activity?.findViewById(R.id.music_activity_btn_back)!!
    }

    companion object {
        fun newInstance(mAct: MainMusicActivity, listener: MainMusicActivity.HandleOnActivity): MyMusicFragment {
            val fragment = MyMusicFragment()
            fragment.mAct = mAct
            fragment.listener = listener
            return fragment
        }
    }

    override fun onStop() {
        playMusicButton.setImageResource(R.drawable.icon_play_music)
        mediaPlayer.pauseSound(null)
        super.onStop()
    }

    override fun onDestroy() {
        mediaPlayer.stopSound()
        super.onDestroy()
    }
}
