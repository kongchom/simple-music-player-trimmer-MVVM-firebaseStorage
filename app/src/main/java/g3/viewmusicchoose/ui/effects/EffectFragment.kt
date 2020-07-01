package g3.viewmusicchoose.ui.effects

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import g3.viewmusicchoose.*
import g3.viewmusicchoose.ui.featured.ui.FeaturedFragmentViewModel
import g3.viewmusicchoose.ui.featured.ui.HotMusicAdapter
import g3.viewmusicchoose.util.MyMediaPlayer
import timber.log.Timber
import javax.inject.Inject

class EffectFragment : Fragment() {


    lateinit var playMusicView: View
    lateinit var btnDownload: View
    lateinit var playMusicButton: ImageView
    lateinit var playMusicTrackTitle: TextView
    lateinit var activityTitle: TextView
    lateinit var activityBackButton: ImageView
    lateinit var playMusicTrackDuration: TextView
    lateinit var effectRecyclerView: RecyclerView
    lateinit var effectDetailsRecyclerView: RecyclerView
    lateinit var effectRvAdapter: EffectAlbumAdapter
    lateinit var effectDetailRvAdapter: HotMusicAdapter
    lateinit var rvLayoutManager: LinearLayoutManager
    lateinit var mediaPlayer: MyMediaPlayer
    lateinit var trimView: CustomTrimView
    lateinit var str: String
    var handler = Handler()

    @Inject
    lateinit var mViewModel: EffectViewModel

    @Inject
    lateinit var featuredFragmentViewModel: FeaturedFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_effect,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        MusicApplication.instance.appComponent.inject(this)
        mediaPlayer = MyMediaPlayer.getInstance(requireContext())
        mViewModel.effectAlbumList.observe(viewLifecycleOwner, Observer {

            effectRvAdapter = EffectAlbumAdapter(RealmUtil.getInstance().getList(EffectAlbum::class.java))
            effectRecyclerView.adapter = effectRvAdapter
            effectRecyclerView.layoutManager = GridLayoutManager(requireContext(),3,GridLayoutManager.VERTICAL,false)

            //effect details rv
            effectRvAdapter.onItemClick = { item, position ->
                initAdapterView(item)

                Timber.d("congnm observe data effect album detail ${item.getName()} ")
                Timber.d("congnm observe data effect album detail ${item.getNumOfTrack()} ")
                Timber.d("congnm observe data effect album detail ${item.getListAudio().size} ")
                effectDetailRvAdapter = HotMusicAdapter(item.getListAudio(),false)
                effectDetailsRecyclerView.adapter = effectDetailRvAdapter
                effectDetailsRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

                effectDetailRvAdapter.onItemClick = { item, position ->
                    if (item.isDownloaded) {
                        effectDetailRvAdapter.setItemSelected(position, isDownloaded = true)
                    } else {
                        pauseCurrentTrack()
                        effectDetailRvAdapter.setItemSelected(position, isDownloaded = false)
                    }
                    initPlayMusicView(item)
                    handleTrim(item)
                    handlePlayPause(item)
                }

                effectDetailRvAdapter.onDownloadClick = { item, position ->
                    featuredFragmentViewModel.downloadCurrentTrack(item.audioFileName,position) { downloadSucceed ->
                        if (downloadSucceed) {
                            effectDetailRvAdapter.setItemSelected(position, isDownloaded = true)
                            //init play music view + play music
                            Toast.makeText(context,R.string.download_succeed,Toast.LENGTH_SHORT).show()
                            mediaPlayer.playSound(item.audioFileName, null)
                            playMusicButton.setImageResource(R.drawable.icon_pause)
                        } else {
                            effectDetailRvAdapter.setItemSelected(position, isDownloaded = false)
                            Toast.makeText(context,R.string.download_fail,Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        })

    }

    private fun initAdapterView(item: EffectAlbum) {
        effectRecyclerView.visibility = View.GONE
        effectDetailsRecyclerView.visibility = View.VISIBLE
        activityTitle.text = item.getName()
        activityBackButton.setOnClickListener {
            activityTitle.text = getString(R.string.activity_title)
            effectRecyclerView.visibility = View.VISIBLE
            effectDetailsRecyclerView.visibility = View.GONE
        }
    }

    private fun pauseCurrentTrack() {
        if (mediaPlayer.checkNotNull() && mediaPlayer.playingState) {
            mediaPlayer.pauseSound(null)
            playMusicButton.setImageResource(R.drawable.icon_play_music)
        }
    }

    private fun handlePlayPause(item: Music) {
        playMusicButton.setOnClickListener {
            if (mediaPlayer.checkNotNull() && mediaPlayer.playingState) {
                mediaPlayer.pauseSound(null)
                playMusicButton.setImageResource(R.drawable.icon_play_music)
                Timber.d("congnm observe play")
            } else {
                Timber.d("congnm observe pause")
                if (!item.isDownloaded) {
                    Toast.makeText(context, R.string.please_download_before_playing,Toast.LENGTH_LONG).show()
                } else {
                    Timber.d("congnm on pause featured fragment")
                    mediaPlayer.playSound(item.audioFileName,null)
                    playMusicButton.setImageResource(R.drawable.icon_pause)
                }
            }
        }
    }

    private fun handleTrim(item: Music) {
        trimView.setDuration(item.duration)
        trimView.setOnTrimListener { start, end ->
            Timber.d("congnm on trim listener start $start - end $end")
            mediaPlayer.seekTo(start)
            handler.postDelayed( {
                mediaPlayer.pauseSound(handler)
                playMusicButton.setImageResource(R.drawable.icon_play_music)
            },((end - start) * 1000).toLong())
        }
    }

    private fun initViews() {
        effectRecyclerView = requireView().findViewById(R.id.effect_fragment_rv)
        effectDetailsRecyclerView = requireView().findViewById(R.id.effect_fragment_effect_details_rv)
        playMusicView = activity?.findViewById(R.id.music_activity_play_music_container)!!
        playMusicButton = activity?.findViewById(R.id.play_music_button)!!
        playMusicTrackTitle = activity?.findViewById(R.id.play_music_track_title)!!
        playMusicTrackDuration = activity?.findViewById(R.id.play_music_track_duration)!!
        activityTitle = activity?.findViewById(R.id.music_activity_title)!!
        activityBackButton = activity?.findViewById(R.id.music_activity_btn_back)!!
        trimView = activity?.findViewById(R.id.music_activity_trim_view)!!
    }

    private fun initPlayMusicView(item: Music) {
        playMusicView.visibility = View.VISIBLE
        playMusicTrackTitle.text = item.name
        playMusicTrackDuration.text = item.durationText
    }
}