package g3.viewmusicchoose;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener, OnReceiveMusicListener {

    private static final String TAG = "MusicActivity1";
//    private CustomViewPager mViewPager;

    private MusicViewpagerAdapter mMusicAdapter;
    private LinearLayout mLayoutBack;
    private ImageView mImgBack;
    private TabLayout mTabsStrip;

    private TabLayout mTabsGallery;
    private ViewPager mViewPager;
    private View mIndicator;

    private int indicatorWidth;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_MUSIC = 101;
    public static final int MY_RESULT_CODE_CHOOSE_MUSIC = 103;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        initView();
    }

    private void initView() {
        mImgBack = (ImageView) findViewById(R.id.imgBack);
        ResizeView.resizeView(mImgBack, 25, 50);
        mLayoutBack = findViewById(R.id.music_layout_back);
        mLayoutBack.setOnClickListener(this);

        mMusicAdapter = new MusicViewpagerAdapter(this, getSupportFragmentManager(), this);
        mViewPager = findViewById(R.id.viewpager_tab_music);
        mViewPager.setAdapter(mMusicAdapter);
        mIndicator = findViewById(R.id.indicator);

        mTabsGallery = findViewById(R.id.tabs_music);
        mTabsGallery.setupWithViewPager(mViewPager);

        //Determine indicator width at runtime
        mTabsGallery.post(new Runnable() {
            @Override
            public void run() {
                indicatorWidth = mTabsGallery.getWidth() / mTabsGallery.getTabCount();

                //Assign new width
                FrameLayout.LayoutParams indicatorParams = (FrameLayout.LayoutParams) mIndicator.getLayoutParams();
                indicatorParams.width = indicatorWidth;
                mIndicator.setLayoutParams(indicatorParams);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            //To move the indicator as the user scroll, we will need the scroll offset values
            //positionOffset is a value from [0..1] which represents how far the page has been scrolled
            //see https://developer.android.com/reference/android/support/v4/view/ViewPager.OnPageChangeListener
            @Override
            public void onPageScrolled(int i, float positionOffset, int positionOffsetPx) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mIndicator.getLayoutParams();

                //Multiply positionOffset with indicatorWidth to get translation
                float translationOffset = (positionOffset + i) * indicatorWidth;
                params.leftMargin = (int) translationOffset;
                mIndicator.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    // Pause locale if song is playing
                    Fragment page = getTabFragment(1);
                    if (page != null && page instanceof MusicLocalFragment) {
                        ((MusicLocalFragment) page).releaseMusic();
                    }
                } else {
                    MusicOnlineFragment page = getMusicFragment();
                    if (page != null) {
                        if (page.isThreadDownloadRunning()) {
                            page.showDialogStopDownloadMusic();
                        } else {
                            page.releaseMusic();
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        onPermissionFolder();
    }

    public void onPermissionFolder() {
        Log.e(TAG,"onPermissionFolder start");
        PermissionNewVideoUtils.askForPermissionFolder(this,
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_MUSIC,
                new PermissionListener() {
                    @Override
                    public void onDonePermission() {
                        Log.e(TAG,"onPermissionFolder onDonePermission");
                        if (mMusicAdapter.getItem(1) != null) {
                            Log.e(TAG,"mMusicAdapter.getItem(1) != null");
                            if (mMusicAdapter.getItem(1) instanceof MusicLocalFragment) {
                                Log.e(TAG,"mMusicAdapter.getItem(1) instanceof MusicLocalFragment");
                                ((MusicLocalFragment) mMusicAdapter.getItem(1)).getDataMusicLocal();
                            }
                        }
//                        if (mMusicAdapter.getItem(1) != null && mMusicAdapter.getItem(1) instanceof MusicLocalFragment) {
//                            ((MusicLocalFragment) mMusicAdapter.getItem(1)).getDataMusicLocal();
//                        }
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_MUSIC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                if (mMusicAdapter.getItem(1) != null && mMusicAdapter.getItem(1) instanceof MusicLocalFragment) {
//                    ((MusicLocalFragment) mMusicAdapter.getItem(1)).getDataMusicLocal();
//                }
            } else {
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (isAudioDownloading()) {
            showDialogPauseAudio();
        } else {
            super.onBackPressed();
        }
    }

    private MusicOnlineFragment getMusicFragment() {
        Fragment fragment = getTabFragment(0);
        if (fragment != null && fragment instanceof MusicOnlineFragment) {
            return (MusicOnlineFragment) fragment;
        }
        return null;
    }

    private Fragment getTabFragment(int index) {
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager_tab_music + ":" + index);
    }

    private boolean isAudioDownloading() {
        MusicOnlineFragment fragment = getMusicFragment();
        if (fragment != null) {
            return fragment.isThreadDownloadRunning();
        }
        return false;
    }

    private void stopDownloadMusic() {
        MusicOnlineFragment fragment = getMusicFragment();
        if (fragment != null) {
            fragment.stopDownload();
        }
    }

    private void pauseDownloadMusic() {
        MusicOnlineFragment fragment = getMusicFragment();
        if (fragment != null) {
            fragment.pauseDownload();
        }
    }

    private void resumeDownloadMusic() {
        MusicOnlineFragment fragment = getMusicFragment();
        if (fragment != null) {
            fragment.resumeDownload();
        }
    }

    /**
     * Public method call from activity and Fragment
     */
    public void showDialogPauseAudio() {
        pauseDownloadMusic();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("dialog_stop_download_music");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopDownloadMusic();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Resume Download
                resumeDownloadMusic();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void receive(String name, int duration, String path, int startTime, int endTime) {
        // Setup Bundle
        Intent resultIntent = new Intent();
        resultIntent.putExtra("path", path);
        resultIntent.putExtra("name", name);
        resultIntent.putExtra("start", startTime);
        resultIntent.putExtra("end", endTime);
        resultIntent.putExtra("duration", duration);
        // Result
        finishAndResult(resultIntent);
    }

//    private void showFullAd(OnAdsListener onAdsListener) {
//
//        if (AdManager.getInstance().isShowInterstitialFacebook()) {
//            // SHow dialog
//            final androidx.appcompat.app.AlertDialog dialog = DialogFullAD.showLoadingDialog(MusicActivity.this);
//            ThreadUtils.getInstance().runUIDelay(() -> {
//                DialogFullAD.dismissDialog(MusicActivity.this, dialog);
//                // Show fb
//                AdManager.getInstance().showInterstitialAd(onAdsListener);
//            }, GlobalDef.FACEBOOK_DELAY_TIME);
//
//        } else {
//            AdManager.getInstance().showInterstitialAd(onAdsListener);
//        }
//    }

    private void finishAndResult(Intent resultIntent) {
        if (FunctionUtils.haveNetworkConnection(this)) {
            finishToCallerScreen(resultIntent);

//            if (InterstitialAdTimer.getInstance().isRunning()) {
//                finishToCallerScreen(resultIntent);
//            } else {
//                if (InterstitialAdTimer.getInstance().canbeShown()) {
//                    showFullAd(new OnAdsListener() {
//                        @Override
//                        public void OnCloseAds() {
//                            finishToCallerScreen(resultIntent);
//                        }
//
//                        @Override
//                        public void OnLoadFail() {
//                        }
//
//                        @Override
//                        public void OnLoaded(AdNetworks adNetworks) {
//                        }
//
//                        @Override
//                        public void OnDisplayed() {
//                            DecreaseRPM.denyClick();
//                        }
//                    });
//                } else {
//                    finishToCallerScreen(resultIntent);
//                }
//            }
        } else {
            finishToCallerScreen(resultIntent);
        }
    }

    private void finishToCallerScreen(Intent resultIntent) {
//        InterstitialAdTimer.getInstance().start();
        setResult(MY_RESULT_CODE_CHOOSE_MUSIC, resultIntent);
        finish();
    }

    private ProgressDialog mProgressDialog;
    public void showProgressDialog() {
        showProgressDialog(null);
    }

    public void showProgressDialog(String msg) {
        if (null == mProgressDialog) {
            final int version = Build.VERSION.SDK_INT;
            if (version > 17) {
                mProgressDialog = new ProgressDialog(this, R.style.DialogStyleTransparent);
            } else {
                mProgressDialog = new ProgressDialog(this);
            }
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);

        }
        // SHow dialog
        if (!mProgressDialog.isShowing() && !isFinishing()) {
            if (Build.VERSION.SDK_INT >= 17 && isDestroyed()) {
                return;
            }

            String message = FunctionUtils.isBlank(msg) ? "" : msg;
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (!isFinishing()
                && mProgressDialog != null && mProgressDialog.isShowing()) {
            if (Build.VERSION.SDK_INT >= 17 && isDestroyed()) {
                return;
            }

            mProgressDialog.dismiss();
        }
    }

    public void showDialogNotify(String message) {
        final Dialog dlNotify = new Dialog(this);
        dlNotify.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlNotify.setContentView(R.layout.v3_dialog_notify);
        TextView tvMessage = (TextView) dlNotify.findViewById(R.id.tvMessage);
        Button btnClose = (Button) dlNotify.findViewById(R.id.btnClose);
        tvMessage.setText(message);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlNotify.dismiss();
            }
        });
        dlNotify.show();
    }


    public static boolean isConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isConnected();
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public boolean checkInternetConnection() {
        return isConnected(this);
    }

    public void showDialogCheckInternetConnection() {
        showDialogNotify(getString(R.string.message_network_not_available));
    }


}
