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
import g3.viewmusicchoose.CustomProgressBar
import g3.viewmusicchoose.CustomTrimView
import g3.viewmusicchoose.MusicApplication
import g3.viewmusicchoose.R
import g3.viewmusicchoose.ui.MainMusicActivity
import g3.viewmusicchoose.ui.MainMusicViewModel
import g3.viewmusicchoose.ui.featured.ui.HotMusicAdapter
import g3.viewmusicchoose.util.MyMediaPlayer
import timber.log.Timber
import javax.inject.Inject

class EffectFragment : Fragment() {


    lateinit var effectRecyclerView: RecyclerView
    lateinit var effectDetailsRecyclerView: RecyclerView
    lateinit var effectRvAdapter: EffectAlbumAdapter
    lateinit var effectDetailRvAdapter: HotMusicAdapter
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
                effectDetailRvAdapter = HotMusicAdapter(it[position].getListEffectAudio(),false)
                effectDetailsRecyclerView.adapter = effectDetailRvAdapter
                effectDetailsRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

                effectDetailRvAdapter.onItemClick = { item, position ->
                    //Set play view = visible, set data for play music view
                    mAct.initPlayMusicView(item)
                    //check is same track?
                    if (position != effectDetailRvAdapter.lastPosition) {
                        //Set item is selected
                        effectDetailRvAdapter.setItemSelected(position)
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
                                    Toast.makeText(context,R.string.download_succeed, Toast.LENGTH_SHORT).show()
                                    effectDetailRvAdapter.setItemDownloaded(position)
                                } else {
                                    //in case download fail: show toast
                                    mAct.setShowLoadingVm(false)
                                    Toast.makeText(context,R.string.download_fail, Toast.LENGTH_LONG).show()
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
            }
        })

    }

    private fun initViews() {
        effectRecyclerView = requireView().findViewById(R.id.effect_fragment_rv)
        effectDetailsRecyclerView = requireView().findViewById(R.id.effect_fragment_effect_details_rv)
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