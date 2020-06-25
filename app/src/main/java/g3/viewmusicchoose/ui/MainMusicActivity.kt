package g3.viewmusicchoose.ui

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
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
import g3.viewmusicchoose.ui.featured.ui.FeaturedFragment
import g3.viewmusicchoose.util.AppConstant
import g3.viewmusicchoose.util.AppConstant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
import g3.viewmusicchoose.util.AppConstant.TAB_LAYOUT_SIZE
import g3.viewmusicchoose.util.DialogUtil
import g3.viewmusicchoose.util.DialogUtil.showDenyDialog
import g3.viewmusicchoose.util.DialogUtil.showDialogConfirm
import kotlinx.android.synthetic.main.activity_main_music.*
import timber.log.Timber
import java.lang.System.exit
import javax.inject.Inject

class MainMusicActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    @Inject
    lateinit var mViewModel: MainMusicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_music)
        MusicApplication.instance.appComponent.inject(this)
        initViews()
        requestWriteStoragePermission()
        mViewModel.isShowErrorScreen.observe(this, Observer { needToShowErrorScreen ->
            if (needToShowErrorScreen) {
                Timber.d("congnm show error true")
                network_error_view_container.visibility = View.VISIBLE
            } else {
                Timber.d("congnm show error false")
                network_error_view_container.visibility = View.GONE
            }
        })
        initViewPager()
        initListeners()
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
                    0 -> FeaturedFragment()
                    1 -> MyMusicFragment()
                    2 -> EffectFragment()
                    else -> FeaturedFragment()
                }
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
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

    override fun onStop() {
        Timber.d("congnm onStop")
        SharePrefUtils.putInt(GlobalDef.SHARF_RELOAD_LIST_AUDIO, SharePrefUtils.getInt(GlobalDef.SHARF_RELOAD_LIST_AUDIO,0).plus(1))
        super.onStop()
    }
}

