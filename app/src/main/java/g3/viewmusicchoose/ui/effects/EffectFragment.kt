package g3.viewmusicchoose.ui.effects

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import g3.viewmusicchoose.MusicApplication
import g3.viewmusicchoose.R
import g3.viewmusicchoose.ui.MainMusicActivity
import g3.viewmusicchoose.ui.MainMusicViewModel
import g3.viewmusicchoose.ui.featured.ui.HotMusicAdapter
import g3.viewmusicchoose.util.MyMediaPlayer
import kotlinx.android.synthetic.main.fragment_effect.*
import timber.log.Timber
import javax.inject.Inject

class EffectFragment : Fragment() {


    lateinit var effectRecyclerView: RecyclerView
    lateinit var effectDetailsRecyclerView: RecyclerView
    lateinit var effectRvAdapter: EffectAlbumAdapter
    lateinit var effectDetailRvAdapter: HotMusicAdapter
    private var previousEffectDetailAdapter: HotMusicAdapter? = null
    lateinit var mediaPlayer: MyMediaPlayer
    private lateinit var mAct: MainMusicActivity
    private lateinit var listener: MainMusicActivity.HandleOnActivity
    var handler = Handler()

    @Inject
    lateinit var mViewModel: EffectViewModel

    @Inject
    lateinit var mainMusicViewModel: MainMusicViewModel

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

            effectRvAdapter = EffectAlbumAdapter(it)
            effectRecyclerView.adapter = effectRvAdapter
            effectRecyclerView.layoutManager = GridLayoutManager(requireContext(),3,GridLayoutManager.VERTICAL,false)

            //effect details rv
            effectRvAdapter.onItemClick = { item, position ->
                mAct.initEffectDetailsView(item)
                listener.onChangeAlbum(isInHotMusic = false, isInHotAlbum = false, isInMyMusic = false, isInEffectAlbum = true)
                effectDetailRvAdapter = HotMusicAdapter(it[position].getListEffectAudio(),false)
                effectDetailsRecyclerView.adapter = effectDetailRvAdapter
                listener.onChangeAdapter(effectDetailRvAdapter,null)
                effectDetailsRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

                effectDetailRvAdapter.onItemClick = { item, position ->
                    //Set play view = visible, set data for play music view
                    previousEffectDetailAdapter?.resetSelectedState()
                    previousEffectDetailAdapter = effectDetailRvAdapter
                    mAct.initPlayMusicView(item)
                    //Reset select state of other adapters
                    listener.onChangeAlbum(isInHotMusic = false, isInHotAlbum = false, isInMyMusic = false, isInEffectAlbum = true)
                    listener.onChangeAdapter(effectDetailRvAdapter,null)
                    listener.onClearSelectedState()
                    //check is same track?
                    if (position != effectDetailRvAdapter.lastPosition) {
                        //Set item is selected
                        effectDetailRvAdapter.setItemSelected(position)
                        //If any current track playing -> stop
                        mAct.checkCurrentTrackIsPlaying()
                        //Check file is downloaded?
                        if (item.isDownloaded && mainMusicViewModel.checkFileExist(item)) {
                            Timber.d("congnm play file featured fragment")
                            mAct.playSelectedTrack(item)
                        } else {
                            //in case item is not downloaded yet
                            //download file
                            mAct.setShowLoadingVm(true)
                            mAct.downloadCurrentTrack(item,position,effectDetailRvAdapter)
                        }
                    } else {
                        //in case is same track = true
                        if (!item.isDownloaded && !mainMusicViewModel.checkFileExist(item)) {
                            mAct.downloadCurrentTrack(item,position,effectDetailRvAdapter)
                        }
                    }
                    //listener for play pause button and trim view
                    mAct.handlePlayPause(item)
                    mAct.handleTrim(item)
                }
            }
        })

        mAct.mViewModel.isShowErrorScreen.observe(viewLifecycleOwner, Observer { needToShowErrorScreen ->
            if (needToShowErrorScreen) {
                effect_fragment_error_view_container.visibility = View.VISIBLE
                effect_fragment_rv.visibility = View.GONE
            } else {
                mViewModel.initData()
                effect_fragment_error_view_container.visibility = View.GONE
                effect_fragment_rv.visibility = View.VISIBLE
            }
        })
    }

    private fun initViews() {
        effectRecyclerView = requireView().findViewById(R.id.effect_fragment_rv)
        effectDetailsRecyclerView = requireView().findViewById(R.id.effect_fragment_effect_details_rv)
        fragment_effect_try_again.setOnClickListener {
            mAct.mViewModel.initData()
        }
    }

    companion object {
        fun newInstance(mAct: MainMusicActivity, listener: MainMusicActivity.HandleOnActivity): EffectFragment {
            val fragment = EffectFragment()
            fragment.mAct = mAct
            fragment.listener = listener
            return fragment
        }
    }
}