package g3.viewmusicchoose.ui.featured.ui

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import g3.viewmusicchoose.*
import g3.viewmusicchoose.ui.MainMusicActivity
import g3.viewmusicchoose.ui.effects.EffectViewModel
import g3.viewmusicchoose.ui.featured.model.Album
import g3.viewmusicchoose.util.MyMediaPlayer
import kotlinx.android.synthetic.main.fragment_featured.*
import timber.log.Timber
import javax.inject.Inject

class FeaturedFragment : Fragment() {

    lateinit var mViewModel: FeaturedFragmentViewModel
        @Inject set

    @Inject
    lateinit var effectViewModel: EffectViewModel

    private lateinit var hotMusicRv: RecyclerView
    private lateinit var albumDetailsRv: FrameLayout
    private lateinit var hotMusicAdapter: HotMusicAdapter
    private lateinit var hotAlbumAdapter: HotAlbumAdapter
    private lateinit var hotAlbumItemAdapter: HotMusicAdapter
    private lateinit var mAct: MainMusicActivity
    private lateinit var listener: MainMusicActivity.HandleOnActivity
    lateinit var playMusicView: View
    lateinit var trimView: CustomTrimView
    lateinit var btnDownload: View
    lateinit var playMusicButton: ImageView
    lateinit var playMusicTrackTitle: TextView
    lateinit var activityTitle: TextView
    lateinit var activityBackButton: ImageView
    lateinit var activityToolBar: LinearLayout
    lateinit var playMusicTrackDuration: TextView
    lateinit var rvLayoutManager: LinearLayoutManager
    lateinit var mProgressDialog: CustomProgressBar
    var handler = Handler()
    var onClickHotAlbumListener: OnClickHotAlbumListener? = null


    lateinit var mediaPlayer: MyMediaPlayer

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
        mediaPlayer = MyMediaPlayer.getInstance(requireContext())
        initView()
        mViewModel.initData()
        observeData()
    }

    private fun observeData() {
        mViewModel.hotMusicList.observe(requireActivity(), Observer {
            Timber.d("congnm onObserve hot music ${it.size}")
            hotMusicAdapter = HotMusicAdapter(it,true)
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
            /**
             * Handle on hot music item click
             */
            hotMusicAdapter.onItemClick = { item, position ->
                //Set play view = visible, set data for play music view
                initPlayMusicView(item)
                //check is same track?
                if (position != hotMusicAdapter.lastPosition) {
                    //Set item is selected
                    hotMusicAdapter.setItemSelected(position)
                    //If any current track playing -> stop
                    if (mediaPlayer.checkNotNull() && mediaPlayer.playingState) {
                        mediaPlayer.stopSound()
                    }
                    //Check file is downloaded?
                    if (item.isDownloaded) {
                        Timber.d("congnm play file featured fragment")
                        playSelectedTrack(item)
                    } else {
                        //in case item is not downloaded yet
                        //download file
                        mViewModel.downloadCurrentTrack(item.audioFileName,position) { downloadSucceed ->
                            if (downloadSucceed) {
                                //if download succeed -> do 3 jobs: play music, show toast, set item downloaded = true
                                playSelectedTrack(item)
                                Toast.makeText(context,R.string.download_succeed,Toast.LENGTH_SHORT).show()
                                hotMusicAdapter.setItemDownloaded(position)
                            } else {
                                //in case download fail: show toast
                                Toast.makeText(context,R.string.download_fail,Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    //in case is same track = true
                    //do nothing
                }
                //listener for play pause button and trim view
                handlePlayPause(item)
                handleTrim(item)
            }
        })

        mViewModel.hotAlbumList.observe(requireActivity(), Observer {
            hotAlbumAdapter = HotAlbumAdapter(it)
            featured_fragment_hot_album_rv.adapter = hotAlbumAdapter

            hotAlbumAdapter.onItemClick = { item, position ->
                //set needed views
                initAlbumDetailsView(item)
                hotAlbumItemAdapter = HotMusicAdapter(it[position].getListAudio(),false)
                Timber.d("congnm hot album item adapter size${it[position].getListAudio().size}")
                hot_album_details_rv.adapter = hotAlbumItemAdapter

                hotAlbumItemAdapter.onItemClick = { item, position ->
                    if (item.isDownloaded) {
                        playSelectedTrack(item)
                        hotAlbumItemAdapter.setItemSelected(position)
                    } else {
                        mViewModel.downloadCurrentTrack(item.audioFileName, position) { downloadSucceed ->
                            if (downloadSucceed) {
                                Toast.makeText(context,R.string.download_succeed,Toast.LENGTH_SHORT).show()
                                mediaPlayer.playSound(item.audioFileName, null)
                            } else {
                                Toast.makeText(context,R.string.download_fail,Toast.LENGTH_SHORT).show()
                            }
                        }
                        hotAlbumItemAdapter.setItemSelected(position)
                    }
                    initPlayMusicView(item)
                    handlePlayPause(item)
                    handleTrim(item)
                }

                hot_album_details_rv.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
            }
            featured_fragment_hot_album_rv.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)
        })

        mViewModel.showProgressDialog.observe(requireActivity(), Observer { isShowDialog ->
            if (isShowDialog) {
                Timber.d("congnm show progress dialog")
                mProgressDialog.show(requireContext())
            } else {
                mProgressDialog.dismiss()
            }
        })
    }

    private fun playSelectedTrack(item: Music) {
        mediaPlayer.playSound(item.audioFileName, null)
        playMusicButton.setImageResource(R.drawable.icon_pause)
    }

    private fun pauseCurrentTrack() {
        if (mediaPlayer.checkNotNull() && mediaPlayer.playingState) {
            mediaPlayer.pauseSound(null)
            playMusicButton.setImageResource(R.drawable.icon_play_music)
        }
    }

    private fun initAlbumDetailsView(item: Album) {
        listener.onActivityBackPressed(isInHotAlbum = true, isInEffectAlbum = false)
        fragment_featured_container.visibility = View.GONE
        hot_album_details_rv.visibility = View.VISIBLE
        activityTitle.text = item.getName()
        activityBackButton.setOnClickListener {
            activityTitle.text = getString(R.string.activity_title)
            fragment_featured_container.visibility = View.VISIBLE
            hot_album_details_rv.visibility = View.GONE
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

    private fun initView() {
        hotMusicRv = requireView().findViewById(R.id.featured_fragment_hot_music_rv)
        playMusicView = activity?.findViewById(R.id.music_activity_play_music_container)!!
        playMusicButton = activity?.findViewById(R.id.play_music_button)!!
        playMusicTrackTitle = activity?.findViewById(R.id.play_music_track_title)!!
        playMusicTrackDuration = activity?.findViewById(R.id.play_music_track_duration)!!
        trimView = activity?.findViewById(R.id.music_activity_trim_view)!!
        activityToolBar = requireActivity().findViewById(R.id.music_activity_tool_bar_container)
        activityTitle = activityToolBar.findViewById(R.id.music_activity_title)
        activityBackButton = activityToolBar.findViewById(R.id.music_activity_btn_back)
        mProgressDialog = CustomProgressBar(requireContext())
    }

    private fun initPlayMusicView(item: Music) {
        playMusicView.visibility = View.VISIBLE
        playMusicTrackTitle.text = item.name
        playMusicTrackDuration.text = item.durationText
    }

    override fun onResume() {
        Timber.d("congnm on resume view featured fragment")
        super.onResume()
    }

    override fun onStop() {
        Timber.d("congnm on stop view featured fragment")
        playMusicButton.setImageResource(R.drawable.icon_play_music)
        mediaPlayer.pauseSound(null)
        super.onStop()
    }

    override fun onDestroy() {
        Timber.d("congnm on destroy view featured fragment")
        mediaPlayer.stopSound()
        super.onDestroy()
    }

    interface OnClickHotAlbumListener {
        fun onClickHotAlbum()
    }

    companion object {
        fun newInstance(mAct: MainMusicActivity, listener: MainMusicActivity.HandleOnActivity): FeaturedFragment {
            val fragment = FeaturedFragment()
            fragment.mAct = mAct
            fragment.listener = listener
            return fragment
        }
    }
}
