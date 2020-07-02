package g3.viewmusicchoose.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.BoringLayout
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import g3.viewmusicchoose.ui.effects.EffectFragment
import g3.viewmusicchoose.ui.featured.ui.FeaturedFragment
import g3.viewmusicchoose.ui.mymusic.MyMusicFragment
import g3.viewmusicchoose.util.AppConstant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
import g3.viewmusicchoose.util.AppConstant.TAB_LAYOUT_SIZE
import kotlinx.android.synthetic.main.activity_main_music.*
import kotlinx.android.synthetic.main.fragment_effect.*
import kotlinx.android.synthetic.main.fragment_featured.*
import timber.log.Timber
import javax.inject.Inject

class MainMusicActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    lateinit var activityTitle: TextView
    lateinit var activityBackButton: ImageView
    lateinit var activityToolBar: LinearLayout
    @Inject
    lateinit var mViewModel: MainMusicViewModel
    lateinit var mProgressDialog: CustomProgressBar
    var isInHotAlbum: Boolean = false
    var isInEffectAlbum: Boolean = false

    var listener = object: HandleOnActivity {
        override fun onClickAddButton(
            name: String,
            duration: Int,
            path: String,
            startTime: Int,
            endTime: Int
        ) {

        }

        override fun onActivityBackPressed(isInHotAlbum: Boolean, isInEffectAlbum: Boolean) {
            this@MainMusicActivity.isInHotAlbum = isInHotAlbum
            this@MainMusicActivity.isInEffectAlbum = isInEffectAlbum
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_music)
        MusicApplication.instance.appComponent.inject(this)
        initViews()
        requestWriteStoragePermission()
        observeData()
        initViewPager()
        initListeners()
    }

    private fun observeData() {
        mViewModel.isShowErrorScreen.observe(this, Observer { needToShowErrorScreen ->
            if (needToShowErrorScreen) {
                Timber.d("congnm show error true")
                network_error_view_container.visibility = View.VISIBLE
            } else {
                Timber.d("congnm show error false")
                network_error_view_container.visibility = View.GONE
            }
        })
        mViewModel.showProgressDialog.observe(this, Observer { isShowDialog ->
            if (isShowDialog) {
                Timber.d("congnm show progress dialog")
                mProgressDialog.show(this)
            } else {
                mProgressDialog.dismiss()
            }
        })
    }

    private fun initListeners() {
        main_music_try_again.setOnClickListener {
            mViewModel.initData()
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
                    0 -> FeaturedFragment.newInstance(this@MainMusicActivity, listener)
                    1 -> MyMusicFragment.newInstance(this@MainMusicActivity, listener)
                    2 -> EffectFragment.newInstance(this@MainMusicActivity, listener)
                    else -> FeaturedFragment()
                }
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> activityTitle.text = getString(R.string.activity_title)
                    1 -> activityTitle.text = getString(R.string.activity_title)
                    2 -> activityTitle.text = getString(R.string.activity_title)
                }
            }
        })
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        TabLayoutMediator(tabLayout, viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = when(position) {
                    0 ->  getString(R.string.featured_fragment_title)
                    1 ->  getString(R.string.my_music_fragment_title)
                    2 ->  getString(R.string.effects_fragment_title)
                    else -> ""
                }
            }).attach()
        tabLayout.setTabTextColors(getColor(R.color.colorAccent),getColor(
            R.color.c_ff5f5f
        ))
    }

    private fun initViews() {
        viewPager = findViewById(R.id.music_activity_viewpager)
        tabLayout = findViewById(R.id.music_activity_tab_layout)
        activityToolBar = findViewById(R.id.music_activity_tool_bar_container)
        activityTitle = activityToolBar.findViewById(R.id.music_activity_title)
        activityBackButton = activityToolBar.findViewById(R.id.music_activity_btn_back)
        mProgressDialog = CustomProgressBar(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.isEmpty()) return
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            mViewModel.initData()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                MaterialAlertDialogBuilder(this,R.style.Theme_MaterialComponents_Light_Dialog_Alert)
                    .setTitle(R.string.permission_denied)
                    .setMessage(R.string.message_permission_denied)
                    .setNegativeButton(R.string.exit) { _, _ ->
                        this.finish()
                    }
                    .setPositiveButton(R.string.go_to_setting) { _, _ ->
                        FunctionUtils.openAppSettings(this,this.packageName)
                        this.finish()
                    }.show()
            } else {
                MaterialAlertDialogBuilder(this,R.style.Theme_MaterialComponents_Light_Dialog_Alert)
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
            isInHotAlbum -> {
                activityTitle.text = getString(R.string.activity_title)
                fragment_featured_container.visibility = View.VISIBLE
                hot_album_details_rv.visibility = View.GONE
                isInHotAlbum = false
            }
            isInEffectAlbum -> {
                activityTitle.text = getString(R.string.activity_title)
                effect_fragment_rv.visibility = View.VISIBLE
                effect_fragment_effect_details_rv.visibility = View.GONE
                isInEffectAlbum = false
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onStop() {
        Timber.d("congnm onStop")
        SharePrefUtils.putInt(GlobalDef.SHARF_RELOAD_LIST_AUDIO, SharePrefUtils.getInt(GlobalDef.SHARF_RELOAD_LIST_AUDIO,0).plus(1))
        super.onStop()
    }

    interface HandleOnActivity {
        fun onClickAddButton(name: String,duration: Int,path: String,startTime: Int, endTime: Int)

        fun onActivityBackPressed(isInHotAlbum: Boolean, isInEffectAlbum: Boolean)
    }
}

