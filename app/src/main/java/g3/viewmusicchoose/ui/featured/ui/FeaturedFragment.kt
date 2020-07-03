package g3.viewmusicchoose.ui.featured.ui

import android.os.Bundle
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
import g3.viewmusicchoose.ui.MainMusicViewModel
import g3.viewmusicchoose.ui.featured.model.Album
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
    private lateinit var hotAlbumItemAdapter: HotMusicAdapter
    private lateinit var mAct: MainMusicActivity
    private lateinit var listener: MainMusicActivity.HandleOnActivity
    lateinit var rvLayoutManager: LinearLayoutManager

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

    private fun initView() {
        hotMusicRv = requireView().findViewById(R.id.featured_fragment_hot_music_rv)
        hotAlbumRv = requireView().findViewById(R.id.featured_fragment_hot_album_rv)
        hotAlbumDetailRv = requireView().findViewById(R.id.hot_album_details_rv)
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
                mAct.initPlayMusicView(item)
                //check is same track?
                if (position != hotMusicAdapter.lastPosition) {
                    //Set item is selected
                    hotMusicAdapter.setItemSelected(position)
                    //If any current track playing -> stop
                    if (mediaPlayer.checkNotNull() && mediaPlayer.playingState) {
                        mediaPlayer.stopSound()
                    }
                    //Check file is downloaded?
                    if (item.isDownloaded && mainMusicViewModel.checkFileExist(item) ) {
                        Timber.d("congnm play file featured fragment")
                        mAct.playSelectedTrack(item)
                    } else {
                        //in case item is not downloaded yet
                        //download file
                        mAct.setShowLoadingVm(true)
                        mainMusicViewModel.downloadCurrentTrack(item.audioFileName,position) { downloadSucceed ->
                            if (downloadSucceed) {
                                //if download succeed -> do 3 jobs: play music, show toast, set item downloaded = true
                                mAct.setShowLoadingVm(false)
                                mAct.playSelectedTrack(item)
                                mainMusicViewModel.updateItemHotMusic(item)
                                Toast.makeText(context,R.string.download_succeed,Toast.LENGTH_SHORT).show()
                                hotMusicAdapter.setItemDownloaded(position)
                            } else {
                                //in case download fail: show toast
                                mAct.setShowLoadingVm(false)
                                mAct.playSelectedTrack(item)
                                Toast.makeText(context,R.string.download_fail,Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    //in case is same track = true
                    //do nothing
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
                //set needed views
                mAct.initAlbumDetailsView(item)
                hotAlbumItemAdapter = HotMusicAdapter(it[position].getListAudio(),false)
                Timber.d("congnm hot album item adapter size${it[position].getListAudio().size}")
                hot_album_details_rv.adapter = hotAlbumItemAdapter

                hotAlbumItemAdapter.onItemClick = { item, position ->
                    if (position != hotAlbumItemAdapter.lastPosition) {
                        //Set item is selected
                        hotAlbumItemAdapter.setItemSelected(position)
                        //If any current track playing -> stop
                        if (mediaPlayer.checkNotNull() && mediaPlayer.playingState) {
                            mediaPlayer.stopSound()
                        }
                        //Check file is downloaded?
                        if (item.isDownloaded && mainMusicViewModel.checkFileExist(item)) {
                            Timber.d("congnm play file featured fragment")
                            mAct.playSelectedTrack(item)
                        } else {
                            //in case item is not downloaded yet
                            //download file
                            mAct.setShowLoadingVm(true)
                            mainMusicViewModel.downloadCurrentTrack(item.audioFileName,position) { downloadSucceed ->
                                if (downloadSucceed) {
                                    //if download succeed -> do 3 jobs: play music, show toast, set item downloaded = true
                                    mAct.setShowLoadingVm(false)
                                    mAct.playSelectedTrack(item)
                                    mainMusicViewModel.updateItemHotMusic(item)
                                    Toast.makeText(context,R.string.download_succeed,Toast.LENGTH_SHORT).show()
                                    hotAlbumItemAdapter.setItemDownloaded(position)
                                } else {
                                    //in case download fail: show toast
                                    mAct.setShowLoadingVm(false)
                                    Toast.makeText(context,R.string.download_fail,Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    } else {
                        //in case is same track = true
                        //do nothing
                    }
                    mAct.initPlayMusicView(item)
                    mAct.handlePlayPause(item)
                    mAct.handleTrim(item)
                }

                hot_album_details_rv.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
            }
            featured_fragment_hot_album_rv.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)
        })
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
