package g3.viewmusicchoose.ui.featured.ui

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import g3.viewmusicchoose.GlobalDef
import g3.viewmusicchoose.Music
import g3.viewmusicchoose.MusicApplication
import g3.viewmusicchoose.R
import g3.viewmusicchoose.ui.MainMusicViewModel
import g3.viewmusicchoose.ui.MyMusicFragment
import kotlinx.android.synthetic.main.activity_main_music.*
import kotlinx.android.synthetic.main.fragment_featured.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class FeaturedFragment : Fragment() {

    lateinit var mViewModel: FeaturedFragmentViewModel
        @Inject set

    @Inject
    lateinit var mMainViewModel: MainMusicViewModel


    private lateinit var hotMusicRv: RecyclerView
    private lateinit var albumDetailsRv: FrameLayout
    private lateinit var hotMusicAdapter: HotMusicAdapter
    private lateinit var hotAlbumAdapter: HotAlbumAdapter
    private lateinit var hotAlbumItemAdapter: HotMusicAdapter
    lateinit var playMusicView: View
    lateinit var btnDownload: View
    lateinit var playMusicButton: ImageView
    lateinit var playMusicTrackTitle: TextView
    lateinit var activityTitle: TextView
    lateinit var activityBackButton: ImageView
    lateinit var playMusicTrackDuration: TextView
    lateinit var rvLayoutManager: LinearLayoutManager
    var onClickHotAlbumListener: OnClickHotAlbumListener? = null

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
            rvLayoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
            hotMusicRv.layoutManager = rvLayoutManager

            //make rv looping
            hotMusicRv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val firstItemVisible = rvLayoutManager.findFirstVisibleItemPosition()
                    if (firstItemVisible !=0 && firstItemVisible % it.size == 0) {
                        (hotMusicRv.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    }
                }
            })

            hotMusicAdapter.onItemClick = { item, position ->
                initPlayMusicView(item)
                if (item.isDownloaded && position != hotMusicAdapter.lastPosition) {
                    hotMusicAdapter.setItemSelected(position, isDownloaded = true)
                    //init play music view + play music
                    val myUri: Uri = Uri.fromFile(File(GlobalDef.FOLDER_AUDIO + item.audioFileName))
                    playMusic(myUri)
                } else {
                    hotMusicAdapter.setItemSelected(position, isDownloaded = false)
                    playMusicButton.setImageResource(R.drawable.icon_play_music)
                    mediaPlayer.stop()
                }
            }

            hotMusicAdapter.onDownloadClick = { item, position ->
                mViewModel.downloadCurrentTrack(item.audioFileName,position) { downloadSucceed ->
                    if (downloadSucceed) {
                        hotMusicAdapter.setItemSelected(position, isDownloaded = true)
                        //init play music view + play music
                        val myUri: Uri = Uri.fromFile(File(GlobalDef.FOLDER_AUDIO + item.audioFileName))
                        Toast.makeText(context,R.string.download_succeed,Toast.LENGTH_SHORT).show()
                        playMusic(myUri)
                    } else {
                        hotMusicAdapter.setItemSelected(position, isDownloaded = false)
                        Toast.makeText(context,R.string.download_fail,Toast.LENGTH_LONG).show()
                    }
                }
            }

        })

        mViewModel.hotAlbumList.observe(requireActivity(), Observer {
            hotAlbumAdapter = HotAlbumAdapter(it)
            featured_fragment_hot_album_rv.adapter = hotAlbumAdapter
            hotAlbumAdapter.onItemClick = { item, position ->
                fragment_featured_container.visibility = View.GONE
                hot_album_details_rv.visibility = View.VISIBLE
                activityTitle.text = item.getName()
                hotAlbumItemAdapter = HotMusicAdapter(item.getListAudio())
                hot_album_details_rv.adapter = hotAlbumItemAdapter

                hotAlbumItemAdapter.onItemClick = { item, position ->
                    initPlayMusicView(item)
                    if (item.isDownloaded && position != hotAlbumItemAdapter.lastPosition) {
                        hotAlbumItemAdapter.setItemSelected(position, isDownloaded = true)
                        //init play music view + play music
                        val myUri: Uri = Uri.fromFile(File(GlobalDef.FOLDER_AUDIO + item.audioFileName))
                        playMusic(myUri)
                    } else {
                        hotAlbumItemAdapter.setItemSelected(position, isDownloaded = false)
                        playMusicButton.setImageResource(R.drawable.icon_play_music)
                        mediaPlayer.stop()
                    }
                }

                hot_album_details_rv.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
            }
            featured_fragment_hot_album_rv.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)
        })
    }

    private fun initView() {
        hotMusicRv = requireView().findViewById(R.id.featured_fragment_hot_music_rv)
        playMusicView = activity?.findViewById(R.id.music_activity_play_music_container)!!
        playMusicButton = activity?.findViewById(R.id.play_music_button)!!
        playMusicTrackTitle = activity?.findViewById(R.id.play_music_track_title)!!
        playMusicTrackDuration = activity?.findViewById(R.id.play_music_track_duration)!!
        activityTitle = activity?.findViewById(R.id.music_activity_title)!!
        activityBackButton = activity?.findViewById(R.id.music_activity_btn_back)!!
        activityBackButton.setOnClickListener {
            activityTitle.text = getString(R.string.music_activity_title)
            fragment_featured_container.visibility = View.VISIBLE
        }
    }

    private fun playMusic(uri: Uri) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.release()
        }
        mediaPlayer = MediaPlayer.create(requireContext(),uri)
        mediaPlayer.start()
        playMusicButton.setImageResource(R.drawable.icon_pause)
        Timber.d("congnm play music")
    }

    private fun initPlayMusicView(item: Music) {
        playMusicView.visibility = View.VISIBLE
        playMusicTrackTitle.text = item.name
        playMusicTrackDuration.text = item.durationText
        playMusicButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                playMusicButton.setImageResource(R.drawable.icon_play_music)
                Timber.d("congnm observe play")
            } else {
                Timber.d("congnm observe pause")
                if (!item.isDownloaded) {
                    Toast.makeText(context, R.string.please_download_before_playing,Toast.LENGTH_LONG).show()
                } else {
                    mediaPlayer.start()
                    playMusicButton.setImageResource(R.drawable.icon_pause)
                }
            }
        }
    }

     fun setListenerOnClickHotAlbum(onClickHotAlbumListener: OnClickHotAlbumListener) {
         Timber.d("congnm set album click listener")
        this.onClickHotAlbumListener = onClickHotAlbumListener
    }

    override fun onDestroyView() {
        Timber.d("congnm ondestroy view featured fragment")
        mediaPlayer.release()
        playMusicView.visibility = View.GONE
        super.onDestroyView()
    }

    interface OnClickHotAlbumListener {
        fun onClickHotAlbum()
    }
}
