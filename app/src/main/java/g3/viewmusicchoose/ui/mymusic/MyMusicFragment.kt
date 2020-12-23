package g3.viewmusicchoose.ui.mymusic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import g3.viewmusicchoose.MusicApplication
import g3.viewmusicchoose.MusicUtils
import g3.viewmusicchoose.R
import g3.viewmusicchoose.ui.MainMusicActivity
import g3.viewmusicchoose.ui.MainMusicViewModel
import g3.viewmusicchoose.util.MyMediaPlayer
import javax.inject.Inject

class MyMusicFragment : Fragment() {

    lateinit var myMysicRv: RecyclerView
    lateinit var myMysicRvAdapter: MyMusicAdapter
    lateinit var rvLayoutManager: LinearLayoutManager
    private lateinit var mAct: MainMusicActivity
    private lateinit var listener: MainMusicActivity.HandleOnActivity
    lateinit var mediaPlayer: MyMediaPlayer

    @Inject
    lateinit var mainMusicViewModel: MainMusicViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MusicApplication.instance.appComponent.inject(this)
        mediaPlayer = MyMediaPlayer.getInstance(requireContext())
        initViews()
        val listLocalSong = MusicUtils.bindAllSongs(requireContext())
        myMysicRvAdapter = MyMusicAdapter(listLocalSong)
        myMysicRv.adapter = myMysicRvAdapter
        rvLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        myMysicRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstItemVisible = rvLayoutManager.findFirstVisibleItemPosition()
                if (firstItemVisible != 0 && firstItemVisible % listLocalSong.size == 0) {
                    (myMysicRv.layoutManager as LinearLayoutManager).scrollToPosition(0)
                }
            }
        })
        myMysicRv.layoutManager = rvLayoutManager
        initListener()
    }

    private fun initListener() {
        myMysicRvAdapter.onItemClick = { item, position ->
            //Set play view = visible, set data for play music view
            mAct.initPlayMusicView(item)
            //Reset select state of other adapter
            listener.onChangeAlbum(isInHotMusic = false, isInHotAlbum = false, isInMyMusic = true, isInEffectAlbum = false)
            listener.onChangeAdapter(null,myMysicRvAdapter)
            listener.onClearSelectedState()
            //check is same track?
            if (position != myMysicRvAdapter.lastPosition) {
                //Set item is selected
                myMysicRvAdapter.setItemSelected(position)
                //If any current track playing -> stop
                if (mediaPlayer.checkNotNull() && mediaPlayer.playingState) {
                    mediaPlayer.stopSound()
                }
                mAct.playSelectedTrack(item)
            } else {
                //do nothing
            }
            mAct.handlePlayPause(item)
            mAct.handleTrim(item)
        }
    }

    private fun initViews() {
        myMysicRv = requireView().findViewById(R.id.my_music_recycler_view)
    }

    companion object {
        fun newInstance(
            mAct: MainMusicActivity,
            listener: MainMusicActivity.HandleOnActivity
        ): MyMusicFragment {
            val fragment = MyMusicFragment()
            fragment.mAct = mAct
            fragment.listener = listener
            return fragment
        }
    }
}
