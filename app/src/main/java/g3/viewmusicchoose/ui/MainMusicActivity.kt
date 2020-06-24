package g3.viewmusicchoose.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import g3.viewmusicchoose.MusicApplication
import g3.viewmusicchoose.PermissionNewVideoUtils
import g3.viewmusicchoose.R
import g3.viewmusicchoose.ui.featured.ui.FeaturedFragment
import g3.viewmusicchoose.util.AppConstant
import g3.viewmusicchoose.util.AppConstant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
import g3.viewmusicchoose.util.AppConstant.TAB_LAYOUT_SIZE
import g3.viewmusicchoose.util.DialogUtil
import g3.viewmusicchoose.util.DialogUtil.showDenyDialog
import javax.inject.Inject

class MainMusicActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_music)
        MusicApplication.instance.appComponent.inject(this)
        initViews()
        requestWriteStoragePermission()
    }

    private fun requestWriteStoragePermission() {
        PermissionNewVideoUtils.askForPermissionFolder(
            this,
            AppConstant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
        ) {
            initViewPager()
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
            initViewPager()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                DialogUtil.openAppSettings(
                    this,
                    isCancel = false,
                    isFinishActivity = true
                )
                return
            }
            showDenyDialog(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE,
                isFinishActivity = true,
                isCancel = false
            )
        }
    }
}

