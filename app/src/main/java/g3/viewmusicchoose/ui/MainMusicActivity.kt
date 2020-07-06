package g3.viewmusicchoose.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import g3.viewmusicchoose.*
import g3.viewmusicchoose.ui.effects.EffectAlbum
import g3.viewmusicchoose.ui.effects.EffectFragment
import g3.viewmusicchoose.ui.featured.model.Album
import g3.viewmusicchoose.ui.featured.ui.FeaturedFragment
import g3.viewmusicchoose.ui.featured.ui.HotMusicAdapter
import g3.viewmusicchoose.ui.mymusic.MyMusicAdapter
import g3.viewmusicchoose.ui.mymusic.MyMusicFragment
import g3.viewmusicchoose.util.AppConstant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
import g3.viewmusicchoose.util.AppConstant.MY_RESULT_CODE_CHOOSE_MUSIC
import g3.viewmusicchoose.util.AppConstant.TAB_LAYOUT_SIZE
import g3.viewmusicchoose.util.MyMediaPlayer
import kotlinx.android.synthetic.main.activity_main_music.*
import kotlinx.android.synthetic.main.fragment_effect.*
import kotlinx.android.synthetic.main.fragment_featured.*
import timber.log.Timber
import javax.inject.Inject

class MainMusicActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    lateinit var playMusicView: View
    lateinit var trimView: CustomTrimView
    lateinit var playMusicButton: ImageView
    lateinit var playMusicTrackTitle: TextView
    lateinit var activityTitle: TextView
    lateinit var activityBackButton: ImageView
    lateinit var activityToolBar: LinearLayout
    lateinit var playMusicTrackDuration: TextView
    lateinit var mProgressDialog: CustomProgressBar

    var isInHotAlbum: Boolean = false
    var isInEffectAlbum: Boolean = false
    var isInMyMusic: Boolean = false
    var isInHotMusic: Boolean = false
    var isInTabMyMusic: Boolean = false
    var mCurrentFrag = FeaturedFragment()
    var hotMusicAdapter: HotMusicAdapter? = null
    var hotAlbumItemAdapter: HotMusicAdapter? = null
    var effectItemAdapter: HotMusicAdapter? = null
    var myMusicAdapter: MyMusicAdapter? = null
    var handler = Handler()
    var currentMusicTrack: Music? = null
    var currentLocalSong: LocalSong? = null

    lateinit var mediaPlayer: MyMediaPlayer

    @Inject
    lateinit var mViewModel: MainMusicViewModel

    var listener = object : HandleOnActivity {
        override fun onClickAddButton(
            name: String,
            duration: Int,
            path: String,
            startTime: Int,
            endTime: Int
        ) {
            // Setup Bundle
            val resultIntent = Intent()
            resultIntent.putExtra("path", path)
            resultIntent.putExtra("name", name)
            resultIntent.putExtra("start", startTime)
            resultIntent.putExtra("end", endTime)
            resultIntent.putExtra("duration", duration)
            setResult(MY_RESULT_CODE_CHOOSE_MUSIC, resultIntent)
            finish()
        }

        override fun onChangeAlbum(
            isInHotMusic: Boolean,
            isInHotAlbum: Boolean,
            isInMyMusic: Boolean,
            isInEffectAlbum: Boolean
        ) {
            this@MainMusicActivity.isInHotMusic = isInHotMusic
            this@MainMusicActivity.isInHotAlbum = isInHotAlbum
            this@MainMusicActivity.isInMyMusic = isInMyMusic
            this@MainMusicActivity.isInEffectAlbum = isInEffectAlbum
        }

        override fun onClearSelectedState() {
            if (isInHotMusic) {
                this@MainMusicActivity.hotAlbumItemAdapter?.resetSelectedState()
                this@MainMusicActivity.myMusicAdapter?.resetSelectedState()
                this@MainMusicActivity.effectItemAdapter?.resetSelectedState()
            }
            if (isInHotAlbum) {
                this@MainMusicActivity.hotMusicAdapter?.resetSelectedState()
                this@MainMusicActivity.myMusicAdapter?.resetSelectedState()
                this@MainMusicActivity.effectItemAdapter?.resetSelectedState()
            }
            if (isInMyMusic) {
                Timber.d("set is in my music")
                this@MainMusicActivity.hotAlbumItemAdapter?.resetSelectedState()
                this@MainMusicActivity.hotMusicAdapter?.resetSelectedState()
                this@MainMusicActivity.effectItemAdapter?.resetSelectedState()
            }
            if (isInEffectAlbum) {
                this@MainMusicActivity.hotAlbumItemAdapter?.resetSelectedState()
                this@MainMusicActivity.hotMusicAdapter?.resetSelectedState()
                this@MainMusicActivity.myMusicAdapter?.resetSelectedState()
            }
        }

        override fun onChangeAdapter(
            hotMusicAdapter: HotMusicAdapter?,
            myMusicAdapter: MyMusicAdapter?
        ) {
            if (isInHotMusic) {
                Timber.d("set is in hot music")
                this@MainMusicActivity.hotMusicAdapter = hotMusicAdapter
            }
            if (isInHotAlbum) {
                Timber.d("set is in hot album")
                this@MainMusicActivity.hotAlbumItemAdapter = hotMusicAdapter
            }
            if (isInMyMusic) {
                this@MainMusicActivity.myMusicAdapter = myMusicAdapter
            }
            if (isInEffectAlbum) {
                Timber.d("set is in effect album")
                this@MainMusicActivity.effectItemAdapter = hotMusicAdapter
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_music)
        MusicApplication.instance.appComponent.inject(this)
        mediaPlayer = MyMediaPlayer.getInstance(this)
        initViews()
        requestWriteStoragePermission()
        initListeners()
        initViewPager()
        observeData()
    }

    private fun observeData() {

        mViewModel.showProgressDialog.observe(this, Observer { isShowDialog ->
            if (isShowDialog) {
                Timber.d("congnm show progress dialog")
                mProgressDialog.show(this)
            } else {
                Timber.d("congnm dismiss progress dialog")
                mProgressDialog.dismiss()
            }
        })
    }

    private fun initListeners() {
        music_activity_btn_add.setOnClickListener {
            currentLocalSong?.let {
                listener.onClickAddButton(
                    it.songTitle,
                    it.duration,
                    it.songData,
                    trimView.timeStart,
                    trimView.timeEnd
                )
            }
            currentMusicTrack?.let {
                listener.onClickAddButton(
                    it.name,
                    it.duration,
                    GlobalDef.FOLDER_AUDIO + it.name,
                    trimView.timeStart,
                    trimView.timeEnd
                )
            }
        }
    }

    private fun requestWriteStoragePermission() {
        PermissionNewVideoUtils.askForPermissionFolder(
            this,
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
        ) {
            mViewModel.initData()
        }
    }

    private fun initViewPager() {
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return TAB_LAYOUT_SIZE
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        val frag = FeaturedFragment.newInstance(this@MainMusicActivity, listener)
                        mCurrentFrag = frag
                        return frag
                    }
                    1 -> MyMusicFragment.newInstance(this@MainMusicActivity, listener)
                    2 -> EffectFragment.newInstance(this@MainMusicActivity, listener)
                    else -> FeaturedFragment()
                }
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        if (isInHotAlbum || isInTabMyMusic) {
                            fragment_featured_container.visibility = View.VISIBLE
                            hot_album_details_rv.visibility = View.GONE
                        }
                        if (isInEffectAlbum) {
                            effect_fragment_rv.visibility = View.VISIBLE
                            effect_fragment_effect_details_rv.visibility = View.GONE
                        }
                        activityTitle.text = getString(R.string.activity_title)
                        isInTabMyMusic = false
                    }
                    1 -> {
                        isInTabMyMusic = true
                        activityTitle.text = getString(R.string.activity_title)
                    }
                    2 -> {
                        if (isInHotAlbum) {
                            fragment_featured_container.visibility = View.VISIBLE
                            hot_album_details_rv.visibility = View.GONE
                        }
                        if (isInEffectAlbum || isInTabMyMusic) {
                            effect_fragment_rv.visibility = View.VISIBLE
                            effect_fragment_effect_details_rv.visibility = View.GONE
                        }
                        activityTitle.text = getString(R.string.activity_title)
                        isInTabMyMusic = false
                    }
                }
            }
        })
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        TabLayoutMediator(tabLayout, viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.featured_fragment_title)
                    1 -> getString(R.string.my_music_fragment_title)
                    2 -> getString(R.string.effects_fragment_title)
                    else -> ""
                }
            }).attach()
        tabLayout.setTabTextColors(
            getColor(R.color.colorAccent), getColor(
                R.color.c_ff5f5f
            )
        )
    }

    private fun initViews() {
        viewPager = findViewById(R.id.music_activity_viewpager)
        tabLayout = findViewById(R.id.music_activity_tab_layout)
        playMusicView = findViewById(R.id.music_activity_play_music_container)!!
        playMusicButton = findViewById(R.id.play_music_button)!!
        playMusicTrackTitle = findViewById(R.id.play_music_track_title)!!
        playMusicTrackDuration = findViewById(R.id.play_music_track_duration)!!
        trimView = findViewById(R.id.music_activity_trim_view)!!
        activityToolBar = findViewById(R.id.music_activity_tool_bar_container)
        activityTitle = activityToolBar.findViewById(R.id.music_activity_title)
        activityBackButton = activityToolBar.findViewById(R.id.music_activity_btn_back)
        mProgressDialog = CustomProgressBar(this)
    }

    fun playSelectedTrack(item: Music) {
        currentMusicTrack = item
        currentLocalSong = null
        mediaPlayer.playSound(item.audioFileName, null)
        playMusicButton.setImageResource(R.drawable.icon_pause)
    }

    fun playSelectedTrack(item: LocalSong) {
        currentLocalSong = item
        currentMusicTrack = null
        mediaPlayer.playSound("", item.songData)
        playMusicButton.setImageResource(R.drawable.icon_pause)
    }

    fun initAlbumDetailsView(item: Album) {
        fragment_featured_container.visibility = View.GONE
        hot_album_details_rv.visibility = View.VISIBLE
        activityTitle.text = item.getName()
        activityBackButton.setOnClickListener {
            activityTitle.text = getString(R.string.activity_title)
            fragment_featured_container.visibility = View.VISIBLE
            hot_album_details_rv.visibility = View.GONE
        }
    }

    fun initEffectDetailsView(item: EffectAlbum) {
        activityTitle.text = item.getName()
        effect_fragment_rv.visibility = View.GONE
        effect_fragment_effect_details_rv.visibility = View.VISIBLE
        activityBackButton.setOnClickListener {
            activityTitle.text = getString(R.string.activity_title)
            effect_fragment_rv.visibility = View.VISIBLE
            effect_fragment_effect_details_rv.visibility = View.GONE
        }
    }

    fun handlePlayPause(item: Any) {
        playMusicButton.setOnClickListener {
            if (mediaPlayer.checkNotNull() && mediaPlayer.playingState) {
                mediaPlayer.pauseSound(null)
                playMusicButton.setImageResource(R.drawable.icon_play_music)
            } else {
                if (item is Music) {
                    mediaPlayer.restartSound()
                }
                if (item is LocalSong) {
                    mediaPlayer.restartSound()
                }
                playMusicButton.setImageResource(R.drawable.icon_pause)
            }
        }
    }

    fun handleTrim(item: Any) {
        if (item is Music) {
            trimView.setDuration(item.duration)
        }
        if (item is LocalSong) {
            trimView.setDuration(item.duration)
        }
        trimView.setOnTrimListener { start, end ->
            Timber.d("congnm on trim listener start $start - end $end")
            mediaPlayer.seekTo(start)
            handler.postDelayed({
                mediaPlayer.pauseSound(handler)
                playMusicButton.setImageResource(R.drawable.icon_play_music)
            }, ((end - start) * 1000).toLong())
        }
    }

    fun initPlayMusicView(item: Any) {
        playMusicView.visibility = View.VISIBLE
        if (item is Music) {
            playMusicTrackTitle.text = item.name
            playMusicTrackDuration.text = item.durationText
        }
        if (item is LocalSong) {
            playMusicTrackTitle.text = item.songTitle
            playMusicTrackDuration.text = item.durationText
        }
    }

    fun setShowLoadingVm(show: Boolean) {
        mViewModel.showProgressDialog(show)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.isEmpty()) return
        if (ActivityCompat.checkSelfPermission(
                this,
                permissions[0]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mViewModel.initData()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                MaterialAlertDialogBuilder(
                    this,
                    R.style.Theme_MaterialComponents_Light_Dialog_Alert
                )
                    .setTitle(R.string.permission_denied)
                    .setMessage(R.string.message_permission_denied)
                    .setNegativeButton(R.string.exit) { _, _ ->
                        this.finish()
                    }
                    .setPositiveButton(R.string.go_to_setting) { _, _ ->
                        FunctionUtils.openAppSettings(this, this.packageName)
                        this.finish()
                    }.show()
            } else {
                MaterialAlertDialogBuilder(
                    this,
                    R.style.Theme_MaterialComponents_Light_Dialog_Alert
                )
                    .setTitle(R.string.permission_denied)
                    .setMessage(R.string.message_permission_denied)
                    .setNegativeButton(R.string.re_try) { _, _ ->
                        requestWriteStoragePermission()
                    }
                    .setPositiveButton(R.string.i_am_sure) { _, _ ->
                        this.finish()
                    }.show()
            }
        }
    }


    override fun onBackPressed() {
        when {
            isInHotAlbum && !isInTabMyMusic -> {
                activityTitle.text = getString(R.string.activity_title)
                fragment_featured_container.visibility = View.VISIBLE
                hot_album_details_rv.visibility = View.GONE
            }
            isInEffectAlbum && !isInTabMyMusic -> {
                activityTitle.text = getString(R.string.activity_title)
                effect_fragment_rv.visibility = View.VISIBLE
                effect_fragment_effect_details_rv.visibility = View.GONE
            }
            isInTabMyMusic -> {
                super.onBackPressed()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onStop() {
        Timber.d("congnm onStop")
        playMusicButton.setImageResource(R.drawable.icon_play_music)
        mediaPlayer.pauseSound(null)
        SharePrefUtils.putInt(
            GlobalDef.SHARF_RELOAD_LIST_AUDIO,
            SharePrefUtils.getInt(GlobalDef.SHARF_RELOAD_LIST_AUDIO, 0).plus(1)
        )
        super.onStop()
    }

    override fun onDestroy() {
        mediaPlayer.stopSound()
        super.onDestroy()
    }

    fun downloadCurrentTrack(item: Music, position: Int, adapter: HotMusicAdapter) {
        mViewModel.downloadCurrentTrack(item.audioFileName) { downloadSucceed ->
            if (downloadSucceed) {
                //if download succeed -> do 3 jobs: play music, show toast, set item downloaded = true
                setShowLoadingVm(false)
                initPlayMusicView(item)
                playSelectedTrack(item)
                mViewModel.updateItemHotMusic(item)
                Toast.makeText(this, R.string.download_succeed, Toast.LENGTH_SHORT).show()
                adapter.setItemDownloaded(position)
            } else {
                //in case download fail: show toast
                setShowLoadingVm(false)
                Toast.makeText(this, R.string.download_fail, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun checkCurrentTrackIsPlaying() {
        if (mediaPlayer.checkNotNull() && mediaPlayer.playingState) {
            mediaPlayer.pauseSound(null)
            playMusicButton.setImageResource(R.drawable.icon_play_music)
        }
    }

    interface HandleOnActivity {
        fun onClickAddButton(
            name: String,
            duration: Int,
            path: String,
            startTime: Int,
            endTime: Int
        )

        fun onChangeAlbum(
            isInHotMusic: Boolean,
            isInHotAlbum: Boolean,
            isInMyMusic: Boolean,
            isInEffectAlbum: Boolean
        )

        fun onClearSelectedState()

        fun onChangeAdapter(hotMusicAdapter: HotMusicAdapter?, myMusicAdapter: MyMusicAdapter?)
    }
}

