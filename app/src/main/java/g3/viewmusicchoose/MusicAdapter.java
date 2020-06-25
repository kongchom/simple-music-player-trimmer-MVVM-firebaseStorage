package g3.viewmusicchoose;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
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

import java.io.IOException;
import java.util.List;

/**Music Adapter for MusicLocalFragment*/
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ItemViewHolder> {

    public interface OnClickItemMusicLocalListener {

        void onClickItem(int position, int timeStart, int timeEnd);

        void onReturnPosition(int position);
    }

    private static final String keyAd = System.currentTimeMillis() + "112";
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_AD = 1;
    private Context mContext;
    private List<LocalSong> mMusics;
    private int mCurrentPosition = -1;

    private MediaPlayer mMusicPreview;
    private Handler handler;
    private boolean isPreview;
    private OnClickItemMusicLocalListener mOnClickItemMusicListener;
    private LinearLayout itemAdAdvanced = null;

    int ITEM_SHOW_AD_INDEX_MUSIC = 2;


    public MusicAdapter(Context context, List<LocalSong> musics) {
        this.mContext = context;
        this.mMusics = musics;
        mMusicPreview = new MediaPlayer();
        handler = new Handler();
        isPreview = false;
    }

    public void setOnClickItemMusicListener(OnClickItemMusicLocalListener onClickItemMusicListener) {
        this.mOnClickItemMusicListener = onClickItemMusicListener;
    }

    @Override
    public MusicAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_choose_music_mobile, parent, false);
        return new ItemViewHolder(view);
    }

    private void loadAd(int index, final ItemViewHolder holder) {
        if (holder == null) return;

        if (index == ITEM_SHOW_AD_INDEX_MUSIC) {
            if (itemAdAdvanced == null) {
//                itemAdAdvanced = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_ad_advanced, null);
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
            } else {
                ViewParent viewParent = itemAdAdvanced.getParent();
                if (viewParent != null) {
                    ViewGroup viewGroup = (ViewGroup) viewParent;
                    viewGroup.removeView(itemAdAdvanced);
                }
                holder.layoutAd.addView(itemAdAdvanced);
                holder.layoutAd.setVisibility(View.VISIBLE);
            }

        } else {
            holder.layoutAd.removeAllViews();
            holder.layoutAd.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(MusicAdapter.ItemViewHolder holder, int position) {
        holder.setData(mMusics.get(position));
        loadAd(position, holder);
    }

    @Override
    public int getItemCount() {
        return mMusics != null ? mMusics.size() : 0;
    }

    public void releaseMusicPreview() {
        if (mMusicPreview != null) {
            if (isPreview) mMusicPreview.stop();
            mMusicPreview.release();
            mCurrentPosition = -1;
        }
        isPreview = false;
    }

    public void setCurrentPosition(int currentPosition) {
        this.mCurrentPosition = currentPosition;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements CustomTrimView.OnTrimListener {

        private TextView mTvTime;
        private TextView mTvName;
        private TextView mTvAdd;
        private ImageView mImgPlay;
        private RelativeLayout mRlDetailMusic;
        private LinearLayout mLlContainer;
        private CustomTrimView mTrimView;
        private ImageView ivDisk;
        private LinearLayout layoutAd;

        private RelativeLayout mRlLayoutMusic;

        @SuppressLint("ClickableViewAccessibility")
        ItemViewHolder(View itemView) {
            super(itemView);
            mRlLayoutMusic = itemView.findViewById(R.id.item_choose_music_layout_music);

            mTvTime = (TextView) itemView.findViewById(R.id.item_hot_music_duration);
            mTvName = (TextView) itemView.findViewById(R.id.item_hot_music_name);
            mTvAdd = (TextView) itemView.findViewById(R.id.tvAdd);
            mImgPlay = (ImageView) itemView.findViewById(R.id.imgPlay);
            mRlDetailMusic = (RelativeLayout) itemView.findViewById(R.id.rlDetailMusic);
            mLlContainer = (LinearLayout) itemView.findViewById(R.id.llContainer);
            mTrimView = (CustomTrimView) itemView.findViewById(R.id.trimView);
            ivDisk = (ImageView) itemView.findViewById(R.id.iv_disk);
            layoutAd = (LinearLayout) itemView.findViewById(R.id.linear_ad_music_local);
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

            mLlContainer.setOnClickListener(v -> ItemViewHolder.this.onClickViewItem(getLayoutPosition()));

            mTvAdd.setOnClickListener(v -> {
                if (mOnClickItemMusicListener != null) {
                    mOnClickItemMusicListener.onClickItem(getLayoutPosition(), mTrimView.getTimeStart(), mTrimView.getTimeEnd());
                }
            });

            mImgPlay.setOnClickListener(v -> {
                if (isPreview) {
                    pausePreview();
                } else {
                    resumePreview();
                }
            });
        }

        public void onClickViewItem(int position) {
            if (mCurrentPosition == -1) {
                mMusics.get(position).setSelect(true);
                notifyItemChanged(position);
                mCurrentPosition = position;
                previewMusic(0, mMusics.get(mCurrentPosition).getDuration());
            } else if (mCurrentPosition != position) {
                mMusics.get(mCurrentPosition).setSelect(false);
                mMusics.get(position).setSelect(true);
                notifyItemChanged(mCurrentPosition);
                notifyItemChanged(position);
                stopPreview();
                mCurrentPosition = position;
                previewMusic(0, mMusics.get(mCurrentPosition).getDuration());
            } else {
                if (isPreview) {
                    pausePreview();
                } else {
                    resumePreview();
                }
            }

            if (mOnClickItemMusicListener != null) {
                mOnClickItemMusicListener.onReturnPosition(mCurrentPosition);
            }
        }

        public void pausePreview() {
            isPreview = false;
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            if (mMusicPreview != null) {
                try {
                    mMusicPreview.pause();
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                }
            }
            mImgPlay.setImageResource(R.drawable.icon_play_music);
            ivDisk.setAnimation(null);
        }

        private void resumePreview() {
            if (mMusicPreview != null) {
                mMusicPreview.start();
            }
            isPreview = true;
            mImgPlay.setImageResource(R.drawable.icon_pause);
            RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(700);
            ivDisk.startAnimation(anim);
        }

        public void stopPreview() {
            if (mMusicPreview != null) {
                try {
                    mMusicPreview.stop();
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                }
            }
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            isPreview = false;
            mImgPlay.setImageResource(R.drawable.icon_play_music);
            ivDisk.setAnimation(null);
        }

        private void previewMusic(int start, int end) {
            mMusicPreview.reset();
            RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(700);
            ivDisk.startAnimation(anim);
            try {
                if (mCurrentPosition != -1) {
                    if (mMusics.get(mCurrentPosition).getSongData() != null) {
                        mMusicPreview.setDataSource(mMusics.get(mCurrentPosition).getSongData());
                        mMusicPreview.prepare();
                        mMusicPreview.seekTo(start * 1000);
                        mMusicPreview.start();
                        handler.postDelayed(() -> {
                                    if (isPreview) {
                                        mMusicPreview.pause();
                                    }
                                }, (long) ((end - start) * 1000)
                        );
                        isPreview = true;
                        mImgPlay.setImageResource(R.drawable.icon_pause);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void hideItem() {
            // Update data
            if (mCurrentPosition == -1) {
                mMusics.get(getLayoutPosition()).setSelect(false);
                notifyItemChanged(getLayoutPosition());
                stopPreview();
            } else {
                mMusics.get(mCurrentPosition).setSelect(false);
                notifyItemChanged(mCurrentPosition);
                stopPreview();
                mCurrentPosition = -1;
            }
        }

        void setData(LocalSong music) {
            mTvTime.setText(music.getDurationText());// add text duration
            mTvName.setText(music.getSongTitle()); // add tên bài hát

            mTrimView.setDuration(music.getDuration());
            mImgPlay.setImageResource(music.isSelect() ? R.drawable.icon_pause : R.drawable.icon_play_music);

//            mTvAdd.setVisibility(music.isSelect() ? View.VISIBLE : View.GONE);
//            mRlDetailMusic.setVisibility(music.isSelect() ? View.VISIBLE : View.GONE);

            if (music.isSelect()) {
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
        }


        @Override
        public void onTrim(int start, int end) {
            stopPreview();
            previewMusic(start, end);
        }
    }

    public void destroyAd() {
//        AdManager.getInstance().destroy(keyAd);
    }
}
