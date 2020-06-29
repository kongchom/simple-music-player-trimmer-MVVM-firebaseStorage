package g3.viewmusicchoose.ui.effects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import g3.viewmusicchoose.MusicApplication
import g3.viewmusicchoose.R
import g3.viewmusicchoose.RealmUtil
import g3.viewmusicchoose.ui.featured.model.Album
import g3.viewmusicchoose.ui.featured.ui.HotAlbumAdapter
import g3.viewmusicchoose.ui.mymusic.MyMusicAdapter
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
    lateinit var effectRvAdapter: HotAlbumAdapter
    lateinit var rvLayoutManager: LinearLayoutManager

    lateinit var mViewModel: EffectViewModel
    @Inject set

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
        mViewModel.hotAlbumList.observe(requireActivity(), Observer {
            effectRvAdapter = HotAlbumAdapter(it)
            Timber.d("congnm effect album ${it.size}")
            effectRecyclerView.adapter = effectRvAdapter
            effectRecyclerView.layoutManager = GridLayoutManager(requireContext(),3,GridLayoutManager.VERTICAL,false)
        })
    }

    private fun initViews() {
        effectRecyclerView = requireView().findViewById(R.id.effect_fragment_rv)
        playMusicView = activity?.findViewById(R.id.music_activity_play_music_container)!!
        playMusicButton = activity?.findViewById(R.id.play_music_button)!!
        playMusicTrackTitle = activity?.findViewById(R.id.play_music_track_title)!!
        playMusicTrackDuration = activity?.findViewById(R.id.play_music_track_duration)!!
        activityTitle = activity?.findViewById(R.id.music_activity_title)!!
        activityBackButton = activity?.findViewById(R.id.music_activity_btn_back)!!
    }
}