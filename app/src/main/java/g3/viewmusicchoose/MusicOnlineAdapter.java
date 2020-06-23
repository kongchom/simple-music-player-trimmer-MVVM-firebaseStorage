package g3.viewmusicchoose;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.List;

import timber.log.Timber;

import static g3.viewmusicchoose.GlobalDef.FOLDER_AUDIO;


public class MusicOnlineAdapter extends RecyclerView.Adapter<MusicOnlineAdapter.ItemViewHolder> {
    public interface OnClickItemMusicListener {
        void onClickItemOnline(int position, String path, int timeStart, int timeEnd);

        void onDownLoadMusic(int position);

        void onReturnPosition(int position);
    }

    public static boolean isPauseAudio = false;
    private static final String keyAd = System.currentTimeMillis() + "223";
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_AD = 1;
    private Context mContext;
    private List<Music> mMusics;
    private int mCurrentPosition = -1;
    private OnClickItemMusicListener mOnClickItemMusicListener;
    private MediaPlayer mMusicPreview;
    private Handler handler;
    private boolean isPreview;
    private LinearLayout itemAdAdvanced = null;
    private MusicOnlineFragment hostFragment = null;

    int ITEM_SHOW_AD_INDEX_MUSIC = 2;



    public MusicOnlineAdapter(Context context, List<Music> musics) {
        this.mContext = context;
        this.mMusics = musics;
        mMusicPreview = new MediaPlayer();
        handler = new Handler();
    }

    public void setHostFragment(MusicOnlineFragment fragment) {
        this.hostFragment = fragment;
    }

    private void stopPlayer() {
        if (mMusicPreview != null) {
            try {
                mMusicPreview.stop();
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void releaseMusicPreview() {
        if (mMusicPreview != null) {
            if (isPreview) stopPlayer();
            mMusicPreview.release();
            mCurrentPosition = -1;
        }
        isPreview = false;
    }

    public void setOnClickItemMusicListener(OnClickItemMusicListener onClickItemMusicListener) {
        this.mOnClickItemMusicListener = onClickItemMusicListener;
    }

    @Override
    public MusicOnlineAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_choose_music, parent, false);
        return new ItemViewHolder(view);
    }

    private void loadAd(int index, final ItemViewHolder holder) {
        if (holder == null) return;

        if (index == ITEM_SHOW_AD_INDEX_MUSIC) {
//            if (itemAdAdvanced == null) {
//                itemAdAdvanced = (LinearLayout) LayoutInflater.from(mContext)
//                        .inflate(R.layout.layout_ad_advanced, null);
//
//                final OnAdsListener onAdsListener = new OnAdsListener() {
//                    @Override
//                    public void OnCloseAds() {
//                    }
//
//                    @Override
//                    public void OnLoadFail() {
//                    }
//
//                    @Override
//                    public void OnLoaded(AdNetworks adNetworks) {
//                        ViewParent viewParent = itemAdAdvanced.getParent();
//                        if (viewParent != null) {
//                            ViewGroup viewGroup = (ViewGroup) viewParent;
//                            viewGroup.removeView(itemAdAdvanced);
//                        }
//                        holder.layoutAd.addView(itemAdAdvanced);
//                        holder.layoutAd.setVisibility(View.VISIBLE);
//                    }
//                };
//
//                // Show ad
//                if (AdManager.getInstance().isAdvanced()) {
//                    AdManager.getInstance().createAdvancedAndAddView((Activity) mContext,
//                            itemAdAdvanced,
//                            CustomLayoutAdvanced.getAdMobView100dp((Activity) mContext),
//                            CustomLayoutAdvanced.getFacebookView100dp((Activity) mContext),
//                            AdSizeAdvanced.HEIGHT_100DP,
//                            onAdsListener,
//                            keyAd);
//                }
//            } else {
//                ViewParent viewParent = itemAdAdvanced.getParent();
//                if (viewParent != null) {
//                    ViewGroup viewGroup = (ViewGroup) viewParent;
//                    viewGroup.removeView(itemAdAdvanced);
//                }
//                holder.layoutAd.addView(itemAdAdvanced);
//                holder.layoutAd.setVisibility(View.VISIBLE);
//            }

        } else {
            holder.layoutAd.removeAllViews();
            holder.layoutAd.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(MusicOnlineAdapter.ItemViewHolder holder, int position) {
        holder.setData(mMusics.get(position));
        loadAd(position, holder);
    }

    @Override
    public int getItemCount() {
        return mMusics != null ? mMusics.size() : 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements CustomTrimView.OnTrimListener {
        private TextView mTvTime;
        private TextView mTvName;
        private TextView mTvAdd;

        private RelativeLayout mRlLayoutMusic;
        private RelativeLayout mRlDetailMusic;

        private LinearLayout mLlContainer;
        private CustomTrimView mTrimView;
        private ImageView mImgDown;
        private CircularProgressBar mProgressBar;
        private ImageView mImgPlay;
        private ImageView ivDisk;
        private LinearLayout layoutAd;

        @SuppressLint("ClickableViewAccessibility")
        ItemViewHolder(View itemView) {
            super(itemView);

            mTvTime = (TextView) itemView.findViewById(R.id.tvTime);
            mTvName = (TextView) itemView.findViewById(R.id.tvName);
            mTvAdd = (TextView) itemView.findViewById(R.id.tvAdd);
            mImgPlay = (ImageView) itemView.findViewById(R.id.imgPlay);

            mRlLayoutMusic = itemView.findViewById(R.id.item_choose_music_layout_music);
            mRlDetailMusic = (RelativeLayout) itemView.findViewById(R.id.rlDetailMusic);

            mLlContainer = (LinearLayout) itemView.findViewById(R.id.llContainer);
            mTrimView = (CustomTrimView) itemView.findViewById(R.id.trimView);
            mImgDown = (ImageView) itemView.findViewById(R.id.imgDownMusic);
            mProgressBar = (CircularProgressBar) itemView.findViewById(R.id.circularProgress);
            ivDisk = (ImageView) itemView.findViewById(R.id.iv_disk);
            layoutAd = (LinearLayout) itemView.findViewById(R.id.linear_ad_music_online);
            mTrimView.setOnTrimListener(this);

            mTrimView.setOnTouchListener((v, event) -> {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        //Allow ScrollView to intercept touch events once again.
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                // Handle RecyclerView touch events.
                v.onTouchEvent(event);
                return true;
            });

            // Set click to Audio Download or Play
            mLlContainer.setOnClickListener(v -> {

                // Check music downloading?
                if (hostFragment != null && hostFragment.isThreadDownloadRunning()) {
                    hostFragment.showDialogStopDownloadMusic();
                    return;
                }

                if (mMusics.get(getLayoutPosition()).isDownload()) {
                    if (mCurrentPosition == -1) {
                        mMusics.get(getLayoutPosition()).setSelect(true);
                        mMusics.get(getLayoutPosition()).setLoading(false);
                        mMusics.get(getLayoutPosition()).setDownload(true);
                        notifyItemChanged(getLayoutPosition());
                        mCurrentPosition = getLayoutPosition();
                        mImgPlay.setImageResource(R.drawable.icon_pause);
                        startPreviewMusic(0, mMusics.get(mCurrentPosition).getDuration());
                    } else {
                        if (mCurrentPosition != getLayoutPosition()) {
                            mMusics.get(mCurrentPosition).setSelect(false);
                            mMusics.get(mCurrentPosition).setLoading(false);

                            mMusics.get(getLayoutPosition()).setSelect(true);
                            mMusics.get(getLayoutPosition()).setLoading(false);
                            mMusics.get(getLayoutPosition()).setDownload(true);
                            notifyItemChanged(mCurrentPosition);
                            notifyItemChanged(getLayoutPosition());
                            stopPreview();
                            mCurrentPosition = getLayoutPosition();
                            startPreviewMusic(0, mMusics.get(mCurrentPosition).getDuration());
                        } else {
                            if (isPreview) {
                                pausePreview();
                            } else {
                                startPreviewMusic(0, mMusics.get(mCurrentPosition).getDuration());
                            }
                        }
                    }
                } else {
                    // Not yet download
                    if (hostFragment != null && !hostFragment.haveNetworkConnection()) {
                        hostFragment.showNetworkError();
                        return;
                    }

                    if (mCurrentPosition == -1) {
                        mMusics.get(getLayoutPosition()).setLoading(true);
                        notifyItemChanged(getLayoutPosition());
                    } else {
                        if (mCurrentPosition != getLayoutPosition()) {
                            mMusics.get(mCurrentPosition).setLoading(false);
                            mMusics.get(mCurrentPosition).setSelect(false);

                            mMusics.get(getLayoutPosition()).setLoading(true);
                            notifyItemChanged(mCurrentPosition);
                            notifyItemChanged(getLayoutPosition());
                        } else {
                            mMusics.get(getLayoutPosition()).setLoading(true);
                            notifyItemChanged(getLayoutPosition());
                        }
                    }

                    if (mOnClickItemMusicListener != null) {
                        pausePreview();
                        mOnClickItemMusicListener.onDownLoadMusic(getLayoutPosition());
                    }
                    mCurrentPosition = getLayoutPosition();
                }
                // Set position clicked
                if (mOnClickItemMusicListener != null) {
                    mOnClickItemMusicListener.onReturnPosition(mCurrentPosition);
                }
            });

            mTvAdd.setOnClickListener(v -> {
                if (mOnClickItemMusicListener != null) {
                    if (mCurrentPosition != -1) {
                        String path = FOLDER_AUDIO + mMusics.get(mCurrentPosition).getAudioFileName();
                        mOnClickItemMusicListener.onClickItemOnline(getLayoutPosition(), path, mTrimView.getTimeStart(), mTrimView.getTimeEnd());
                    }
                }
            });

            mImgPlay.setOnClickListener(v -> {
                if (isPreview) {
                    pausePreview();
                } else {
                    if (mCurrentPosition != -1) {
                        startPreviewMusic(0, mMusics.get(mCurrentPosition).getDuration());
                    }
                }
            });
        }

        private void pausePlayer() {
            if (mMusicPreview != null) {
                try {
                    if (mMusicPreview.isPlaying()) {
                        mMusicPreview.pause();
                    }
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void pausePreview() {
            isPreview = false;
            handler.removeCallbacksAndMessages(null);
            pausePlayer();
            mImgPlay.setImageResource(R.drawable.icon_play_music);
            // ivDisk.setAnimation(null);
        }


        public void resumePreview() {
            if (mMusicPreview != null) {
                mMusicPreview.start();

            }
            isPreview = true;
            mImgPlay.setImageResource(R.drawable.icon_pause);
        }

        private void stopPreview() {
            stopPlayer();
            handler.removeCallbacksAndMessages(null);
            isPreview = false;
            mImgPlay.setImageResource(R.drawable.icon_play_music);
            //ivDisk.setAnimation(null);
        }

        public void hideItem() {
            // Update data
            if (mCurrentPosition == -1) {
                mMusics.get(getLayoutPosition()).setSelect(false);
                mMusics.get(getLayoutPosition()).setLoading(false);
                notifyItemChanged(getLayoutPosition());
                stopPreview();
            } else {
                mMusics.get(mCurrentPosition).setSelect(false);
                mMusics.get(mCurrentPosition).setLoading(false);
                notifyItemChanged(mCurrentPosition);
                stopPreview();
                mCurrentPosition = -1;
            }
        }

        public void startPreviewMusic(int start, int end) {
            if (!isPauseAudio) {
                mMusicPreview.reset();
                //Animation();
                try {
                    String path = "";
                    if (mCurrentPosition != -1) {
                        path = FOLDER_AUDIO + mMusics.get(mCurrentPosition).getAudioFileName();
                    }
                    mMusicPreview.setDataSource(path);
                    mMusicPreview.prepare();
                    mMusicPreview.seekTo(start * 1000);
                    mMusicPreview.start();
                    handler.postDelayed(() -> {
                                if (isPreview) {
                                    pausePlayer();
                                }
                            }, (long) ((end - start) * 1000)
                    );
                    isPreview = true;
                    mImgPlay.setImageResource(R.drawable.icon_pause);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void Animation() {
            RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(700);
            ivDisk.startAnimation(anim);
        }

        void setData(Music music) {
            mTvTime.setText(music.getDurationText());
            mTvName.setText(music.getName());
            mTrimView.setDuration(music.getDuration());
            Timber.d(mTvName.getText().toString());

            mProgressBar.setVisibility(music.isLoading() ? View.VISIBLE : View.GONE);
            mProgressBar.setProgress(0);
            mImgPlay.setImageResource(music.isSelect() ? R.drawable.icon_pause : R.drawable.icon_play_music);

            // Check audio is Downloaded?
            String audioPath = FOLDER_AUDIO + music.getAudioFileName();
            File audioFile = new File(audioPath);
            music.setDownload(audioFile.exists());

            if (music.isDownload() && music.isSelect()) {
                mTvAdd.setVisibility(View.VISIBLE);
                mRlDetailMusic.setVisibility(View.VISIBLE);
                mRlLayoutMusic.setBackground(mContext.getResources().getDrawable(R.drawable.d_core_border_music_name_selected));
                mTvAdd.setBackground(mContext.getResources().getDrawable(R.drawable.d_core_border_add_selected));
            } else {
                mRlLayoutMusic.setBackground(mContext.getResources().getDrawable(R.drawable.d_core_border_music_name));
                mTvAdd.setBackground(mContext.getResources().getDrawable(R.drawable.d_core_border_add));
                mTvAdd.setVisibility(View.GONE);
                mRlDetailMusic.setVisibility(View.GONE);
            }

            if (!music.isDownload() && !music.isLoading()) {
                mImgDown.setVisibility(View.VISIBLE);
            } else {
                mImgDown.setVisibility(View.GONE);
            }

//            mImgPlay.setImageResource(R.drawable.ic_pause);

        }

        @Override
        public void onTrim(int start, int end) {
            stopPreview();
            startPreviewMusic(start, end);
        }


        /**
         * Public method call from Fragment
         */
        public void setProgressBar(int progress) {
            if (mProgressBar != null) {
                mProgressBar.setProgress(progress);
            }
        }
    }

    public void destroyAd() {
//        AdManager.getInstance().destroy(keyAd);
    }

}