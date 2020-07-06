package g3.viewmusicchoose.ui.featured.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import g3.viewmusicchoose.MusicApplication
import g3.viewmusicchoose.R
import g3.viewmusicchoose.ui.MainMusicActivity
import g3.viewmusicchoose.ui.MainMusicViewModel
import g3.viewmusicchoose.util.MyMediaPlayer
import kotlinx.android.synthetic.main.fragment_featured.*
import timber.log.Timber
import javax.inject.Inject

class FeaturedFragment : Fragment() {

    @Inject
    lateinit var mViewModel: FeaturedFragmentViewModel

    @Inject
    lateinit var mainMusicViewModel: MainMusicViewModel

    private lateinit var hotMusicRv: RecyclerView
    private lateinit var hotAlbumRv: RecyclerView
    private lateinit var hotAlbumDetailRv: RecyclerView
    private lateinit var hotMusicAdapter: HotMusicAdapter
    private lateinit var hotAlbumAdapter: HotAlbumAdapter
    private var hotAlbumItemAdapter: HotMusicAdapter? = null
    private var previousHotAlbumItemAdapter: HotMusicAdapter? = null
    private lateinit var mAct: MainMusicActivity
    private lateinit var listener: MainMusicActivity.HandleOnActivity
    lateinit var rvLayoutManager: LinearLayoutManager

    lateinit var mediaPlayer: MyMediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_featured, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MusicApplication.instance.appComponent.inject(this)
        mediaPlayer = MyMediaPlayer.getInstance(requireContext())
        initView()
        mViewModel.initData()
        observeData()
    }

    private fun initView() {
        hotMusicRv = requireView().findViewById(R.id.featured_fragment_hot_music_rv)
        hotAlbumRv = requireView().findViewById(R.id.featured_fragment_hot_album_rv)
        hotAlbumDetailRv = requireView().findViewById(R.id.hot_album_details_rv)
        fragment_featured_try_again.setOnClickListener {
            mAct.mViewModel.initData()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun observeData() {
        mViewModel.hotMusicList.observe(requireActivity(), Observer {
            Timber.d("congnm onObserve hot music ${it.size}")
            hotMusicAdapter = HotMusicAdapter(it, true)
            hotMusicRv.adapter = hotMusicAdapter
            rvLayoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            hotMusicRv.layoutManager = rvLayoutManager

            //make rv looping
            hotMusicRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val firstItemVisible = rvLayoutManager.findFirstVisibleItemPosition()
                    if (firstItemVisible != 0 && firstItemVisible % it.size == 0) {
                        (hotMusicRv.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    }
                }
            })
            //disable other scroll on touch hot album rv
//            hotAlbumRv.setOnTouchListener { view, motionEvent ->
//                when (motionEvent.action) {
//                    MotionEvent.ACTION_DOWN,
//                    MotionEvent.ACTION_SCROLL-> mAct.viewPager.beginFakeDrag()
//                    MotionEvent.ACTION_UP -> mAct.viewPager.endFakeDrag()
//                }
//                view.onTouchEvent(motionEvent)
//            }

            /**
             * Handle on hot music item click
             */
            hotMusicAdapter.onItemClick = { item, position ->

                //Reset select state of other adapter
                listener.onChangeAlbum(
                    isInHotMusic = true,
                    isInHotAlbum = false,
                    isInMyMusic = false,
                    isInEffectAlbum = false
                )
                listener.onChangeAdapter(hotMusicAdapter, null)
                listener.onClearSelectedState()
                //check is same track?
                if (position != hotMusicAdapter.lastPosition) {
                    //Set item is selected
                    hotMusicAdapter.setItemSelected(position)
                    //If any current track playing -> stop
                    mAct.checkCurrentTrackIsPlaying()
                    //Check file is downloaded?
                    if (item.isDownloaded && mainMusicViewModel.checkFileExist(item)) {
                        Timber.d("congnm play file featured fragment")
                        //Set play view = visible, set data for play music view
                        mAct.initPlayMusicView(item)
                        mAct.playSelectedTrack(item)
                    } else {
                        //in case item is not downloaded yet
                        //download file
                        mAct.setShowLoadingVm(true)
                        mAct.downloadCurrentTrack(item, position, hotMusicAdapter)
                    }
                } else {
                    //in case is same track = true
                    if (!item.isDownloaded && !mainMusicViewModel.checkFileExist(item)) {
                        mAct.downloadCurrentTrack(item, position, hotMusicAdapter)
                    }
                }
                //listener for play pause button and trim view
                mAct.handlePlayPause(item)
                mAct.handleTrim(item)
            }
        })

        mViewModel.hotAlbumList.observe(requireActivity(), Observer {
            hotAlbumAdapter = HotAlbumAdapter(it)
            featured_fragment_hot_album_rv.adapter = hotAlbumAdapter

            hotAlbumAdapter.onItemClick = { item, position ->
                //set needed views and set isInHotAlbum = true
                mAct.initAlbumDetailsView(item)
                listener.onChangeAlbum(isInHotMusic = false, isInHotAlbum = true, isInMyMusic = false, isInEffectAlbum = false)
                hotAlbumItemAdapter = HotMusicAdapter(it[position].getListAudio(), false)
                hot_album_details_rv.adapter = hotAlbumItemAdapter

                hotAlbumItemAdapter?.onItemClick = { item, position ->
                    //Reset select state of other adapter
                    previousHotAlbumItemAdapter?.resetSelectedState()
                    previousHotAlbumItemAdapter = hotAlbumItemAdapter
                    listener.onChangeAlbum(isInHotMusic = false, isInHotAlbum = true, isInMyMusic = false, isInEffectAlbum = false)
                    listener.onChangeAdapter(hotAlbumItemAdapter, null)
                    listener.onClearSelectedState()
                    if (position != hotAlbumItemAdapter?.lastPosition) {
                        //Set item is selected
                        hotAlbumItemAdapter?.setItemSelected(position)
                        //If any current track playing -> pause
                        mAct.checkCurrentTrackIsPlaying()
                        //Check file is downloaded?
                        if (item.isDownloaded && mainMusicViewModel.checkFileExist(item)) {
                            Timber.d("congnm play file featured fragment")
                            mAct.playSelectedTrack(item)
                        } else {
                            //in case item is not downloaded yet
                            //download file
                            mAct.setShowLoadingVm(true)
                            mAct.downloadCurrentTrack(item, position, hotAlbumItemAdapter!!)
                        }
                    } else {
                        if (!item.isDownloaded && !mainMusicViewModel.checkFileExist(item)) {
                            mAct.downloadCurrentTrack(item, position, hotAlbumItemAdapter!!)
                        }
                    }
                    mAct.initPlayMusicView(item)
                    mAct.handlePlayPause(item)
                    mAct.handleTrim(item)
                }

                hot_album_details_rv.layoutManager =
                    LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            }
            featured_fragment_hot_album_rv.layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        })

        mAct.mViewModel.isShowErrorScreen.observe(viewLifecycleOwner, Observer { needToShowErrorScreen ->
                if (needToShowErrorScreen) {
                    fragment_feature_error_view_container.visibility = View.VISIBLE
                    featured_fragment_tv_hot_album.visibility = View.GONE
                    featured_fragment_tv_hot_music.visibility = View.GONE
                    featured_fragment_hot_album_rv.visibility = View.GONE
                    featured_fragment_hot_music_rv.visibility = View.GONE
                } else {
                    mViewModel.initData()
                    fragment_feature_error_view_container.visibility = View.GONE
                    featured_fragment_tv_hot_album.visibility = View.VISIBLE
                    featured_fragment_tv_hot_music.visibility = View.VISIBLE
                    featured_fragment_hot_album_rv.visibility = View.VISIBLE
                    featured_fragment_hot_music_rv.visibility = View.VISIBLE
                }
            })
    }

    companion object {
        fun newInstance(
            mAct: MainMusicActivity,
            listener: MainMusicActivity.HandleOnActivity
        ): FeaturedFragment {
            val fragment = FeaturedFragment()
            fragment.mAct = mAct
            fragment.listener = listener
            return fragment
        }
    }
}
