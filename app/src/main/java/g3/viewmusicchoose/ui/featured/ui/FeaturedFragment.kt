package g3.viewmusicchoose.ui.featured.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import g3.viewmusicchoose.MusicApplication
import g3.viewmusicchoose.MusicOnlineAdapter
import g3.viewmusicchoose.R
import g3.viewmusicchoose.ui.featured.ui.FeaturedFragmentViewModel
import kotlinx.android.synthetic.main.fragment_featured.*
import timber.log.Timber
import javax.inject.Inject

class FeaturedFragment : Fragment() {

    lateinit var mViewModel: FeaturedFragmentViewModel
    @Inject set
    private lateinit var hotMusicRv: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_featured,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hotMusicRv = requireView().findViewById(R.id.featured_fragment_hot_music_rv)
        MusicApplication.instance.appComponent.inject(this)
        mViewModel.requestStringConfig()
        mViewModel.hotMusicList.observe(requireActivity(), Observer {
            Timber.d("congnm onObserve hot music ${it.size}")
            hotMusicRv.adapter = MusicOnlineAdapter(context, it)
            hotMusicRv.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
        })
        mViewModel.hotAlbumList.observe(requireActivity(), Observer {
            featured_fragment_hot_album_rv.adapter = HotAlbumAdapter(it)
            featured_fragment_hot_album_rv.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)
        })
    }

}
