package g3.viewmusicchoose;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import lib.managerstorage.ManagerStorage;
import lib.managerstorage.OnDownloadFileListener;
import lib.managerstorage.OnGetStringFromUrlListener;

public class MusicOnlineFragment extends Fragment implements MusicOnlineAdapter.OnClickItemMusicListener {

    private static final String TAG = "MusicOnlineFragment";

    private static final String KEY_NAME_TAB = "tab_music";
    private RecyclerView mRecycler;
    private MusicOnlineAdapter mMusicAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<Music> mMusics = new ArrayList<>();
    private List<Music> mMusicOnlines;
    private OnReceiveMusicListener listener;
    private int mCurrentPosition = -1;
    private boolean isDownloading = false;
//
//    private TimeoutUtils timeoutUtils;
//    private FirebaseUtils firebaseUtils;

    private CustomProgressBar mCustomProgressBar;
    Activity mAct;

    public static MusicOnlineFragment initialize(Activity act,String text, OnReceiveMusicListener listener) {
        MusicOnlineFragment fragment = new MusicOnlineFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_NAME_TAB, text);
        fragment.setArguments(bundle);
        fragment.listener = listener;
        fragment.mAct = act;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Get Music from Themes
     */
    public void getDataMusicLocal() {
        mMusics.addAll(ThemeUtils.getListMusicDefault(getActivity()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_online, container, false);
        mRecycler = (RecyclerView) view.findViewById(R.id.recycler);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mMusicAdapter = new MusicOnlineAdapter(getActivity(), mMusics);
        mMusicAdapter.setHostFragment(this);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mMusicAdapter);
        mMusicAdapter.setOnClickItemMusicListener(this);

//        timeoutUtils = TimeoutUtils.newInstance();
//        firebaseUtils = FirebaseUtils.getInstance(getActivity());

        // Get Audio
        getDataMusicLocal();
        loadAudioData();

        //
        mCustomProgressBar = new CustomProgressBar();
        return view;
    }

    /**
     * Load music data
     */
    private void loadAudioData() {
        mMusicOnlines = RealmUtil.getInstance().getList(Music.class);
        if (mMusicOnlines != null && mMusicOnlines.size() != 0) {
            if (FunctionUtils.haveNetworkConnection(getActivity())) {
                // getDataAudioFromFBS
                getDataAudioFromFBS();
            } else {
                // Fill adapter_sticker from Cached
                loadDataFromCache();
            }
        } else {
            if (FunctionUtils.haveNetworkConnection(getActivity())) {
                // getAudio
                requestAudioFromServer();
            } else {
                // no network
                showNetworkError();
            }
        }
    }

    private void loadDataFromCache() {
        // Fill adapter_sticker from Cached
        mMusics.addAll(mMusicOnlines);
        notifyAdapter();
    }

    private void notifyAdapter() {
        if (mMusicAdapter != null) {
            mMusicAdapter.notifyDataSetChanged();
        }
    }

    /**
     * get json audio from fbs
     */
    private void requestAudioFromServer() {
        if (getActivity() != null && !haveNetworkConnection()) {
            if (mMusics.size() <= 0) {
                showNetworkError();
            }
            return;
        }
        // SHow dialog for get audio data
        if (getActivity() != null) {
            showProgressDialog();
        }

        ManagerStorage.getStringFromUrl(getActivity(), GlobalDef.FB_URL_VIDEO_MAKER_AUDIO, new OnGetStringFromUrlListener() {
            @Override
            public void OnSuccessListener(@NotNull String str) {
                if (getActivity() != null) {
                    dismissProgressDialog();
                }

                // Fill data
//                List<Music> musics = null;
//                if (response != null && response.getMusic() != null) {
//                    musics = response.getMusic();
//                }

                Gson gson = new Gson();
                List<Music> musics = gson.fromJson(str, MusicResponse.class).getMusic();

                if (musics != null) {
                    FunctionUtils.createFolder(GlobalDef.FOLDER_AUDIO);

                    for (Music music : musics) {
                        // Append with real local path
                        String audioPath = GlobalDef.FOLDER_AUDIO + music.getAudioFileName();
//                        Lo.d(TAG, "AUDIO_SERVER_NAME = " + music.getName());
//                        Lo.d(TAG, "AUDIO_PATH = " + audioPath);
//                        Lo.d(TAG, "AUDIO_NAME = " + music.getAudioFileName());
                        File localFile = new File(audioPath);
                        // If audio is exist, set flag download to true (don't needed re-download)
                        if (localFile.exists()) {
                            music.setDownloaded(true);
                        }
                    }

                    // Remove and re-set
//                    if (mMusicOnlines != null && !mMusicOnlines.isEmpty()) {
//                        mMusics.removeAll(mMusicOnlines);
//                    }

                    // Set to list and Realm
                    mMusics.addAll(musics);
                    saveAudioData(musics);

                    // reset value cache theme
                    if (getValueCacheAudio() == 1) {
                        setValueCacheAudio(GlobalDef.VALUE_CACHED_RELOAD_LIST_THEME);
                    }

                    notifyAdapter();
                }
            }

            @Override
            public void OnFailListener() {
                if (getActivity() != null) {
                    dismissProgressDialog();

                    if (!haveNetworkConnection()) {
                        showNetworkError();
                    } else {
                        showDialogNotify("error");
                    }
                }
            }
        });

//        ApiClient.getService().getMusicCloud(FunctionUtils.getCurrentLocale()).enqueue(new DataCallBack<MusicResponse>() {
//            @Override
//            public void onSuccess(MusicResponse response) {
//                if (getActivity() != null) {
//                    dismissProgressDialog();
//                }
//
//                // Fill data
//                List<Music> musics = null;
//                if (response != null && response.getMusic() != null) {
//                    musics = response.getMusic();
//                }
//                if (musics != null) {
//                    FunctionUtils.createFolder(GlobalDef.FOLDER_AUDIO);
//
//                    for (Music music : musics) {
//                        // Append with real local path
//                        String audioPath = GlobalDef.FOLDER_AUDIO + music.getAudioFileName();
////                        Lo.d(TAG, "AUDIO_SERVER_NAME = " + music.getName());
////                        Lo.d(TAG, "AUDIO_PATH = " + audioPath);
////                        Lo.d(TAG, "AUDIO_NAME = " + music.getAudioFileName());
//                        File localFile = new File(audioPath);
//                        // If audio is exist, set flag download to true (don't needed re-download)
//                        if (localFile.exists()) {
//                            music.setDownload(true);
//                        }
//                    }
//
//                    // Remove and re-set
////                    if (mMusicOnlines != null && !mMusicOnlines.isEmpty()) {
////                        mMusics.removeAll(mMusicOnlines);
////                    }
//
//                    // Set to list and Realm
//                    mMusics.addAll(musics);
//                    saveAudioData(musics);
//                    // Re-cached values
//                    cachedReloadAudio(CACHE_AUDIO);
//
//                    notifyAdapter();
//                }
//            }
//
//            @Override
//            public void onError(String error) {
//                if (getActivity() != null) {
//                    dismissProgressDialog();
//
//                    if (!haveNetworkConnection()) {
//                        showNetworkError();
//                    } else {
//                        showDialogNotify(error);
//                    }
//                }
//            }
//        });
    }

    private void saveAudioData(List<Music> listMusic) {
        // Delete old values and insert NEW
        ThreadUtils.getInstance().runBackground(new ThreadUtils.IBackground() {
            @Override
            public void doingBackground() {
                Realm realm = Realm.getDefaultInstance();
                try {
                    realm.beginTransaction();
                    RealmResults<Music> eClassTheme = realm.where(Music.class).findAll();
                    eClassTheme.deleteAllFromRealm();
                    realm.copyToRealmOrUpdate(listMusic);
                    realm.commitTransaction();
                } finally {
                    realm.close();
                }
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public void onClickItemOnline(int position, String path, int timeStart, int timeEnd) {
        if (mMusics != null && position < mMusics.size()) {
            Music song = mMusics.get(position);
            if (song != null && listener != null) {
                listener.receive(song.getName(), song.getDuration(), path, timeStart, timeEnd);
            } else {
//                To.show(R.string.msg_music_pick_audio_failed);
            }
        } else {
//            To.show(R.string.msg_music_pick_audio_failed);
        }
    }


    @Override
    public void onDownLoadMusic(int position) {
        if (position == -1 || mMusics == null || position >= mMusics.size()) return;

        mCurrentPosition = position;
        final Music music = mMusics.get(position);
        if (music != null) {
//            final String url = GlobalDef.ROOT_API + mMusics.get(position).getUrl();
//            final String audioName = mMusics.get(position).getAudioFileName();

            final String url = mMusics.get(position).getAudioFileName();
            final String audioName = mMusics.get(position).getAudioFileName();

//            LogUtils.d(TAG, "START DOWNLOAD MUSIC: " + url + " audio name = " + audioName);
            downLoadTheme(url, audioName);
        }
    }

    private void showProgressDialog() {
        if (getActivity() != null && !getActivity().isFinishing()
                && getActivity() instanceof MusicActivity) {
            ((MusicActivity) getActivity()).showProgressDialog();
        }
    }

    private void dismissProgressDialog() {
        if (getActivity() != null && !getActivity().isFinishing()
                && getActivity() instanceof MusicActivity) {
            ((MusicActivity) getActivity()).dismissProgressDialog();
        }
    }

    private void showDialogNotify(String error) {
        if (getActivity() != null && !getActivity().isFinishing()
                && getActivity() instanceof MusicActivity) {
            ((MusicActivity) getActivity()).showDialogNotify(error);
        }
    }

    public boolean haveNetworkConnection() {
        if (getActivity() != null && !getActivity().isFinishing()
                && getActivity() instanceof MusicActivity) {
            return ((MusicActivity) getActivity()).checkInternetConnection();
        }
        return true;
    }

    public void showNetworkError() {
        if (getActivity() != null && !getActivity().isFinishing()
                && getActivity() instanceof MusicActivity) {
            ((MusicActivity) getActivity()).showDialogCheckInternetConnection();
        }
    }

    public void showDialogStopDownloadMusic() {
        if (getActivity() != null && !getActivity().isFinishing()
                && getActivity() instanceof MusicActivity) {
            ((MusicActivity) getActivity()).showDialogPauseAudio();
        }
    }

    public boolean isThreadDownloadRunning() {
//        Lo.d(TAG, "isThreadDownloadRunning: downloadThemeId =" + downloadThemeId);
//        Lo.d(TAG, "isThreadDownloadRunning: isDownloading= " + isDownloading);
//        return isDownloading || (downloadThemeId != -1
//                && Status.RUNNING == PRDownloader.getStatus(downloadThemeId)
//                && Status.CANCELLED != PRDownloader.getStatus(downloadThemeId));
        return false;
    }

    public void stopDownload() {
//        if (downloadThemeId != -1 && (Status.COMPLETED != PRDownloader.getStatus(downloadThemeId))) {
//
//            Lo.d(TAG, "stopDownload: downloadThemeId =" + downloadThemeId);
//            PRDownloader.cancel(downloadThemeId);
//            // Set Music model to default like not yet download
//            if (mCurrentPosition != -1 && mCurrentPosition < mMusics.size()) {
//                mMusics.get(mCurrentPosition).setDownload(false);
//                mMusics.get(mCurrentPosition).setSelect(false);
//                mMusics.get(mCurrentPosition).setLoading(false);
//            }
//            // Notify item still download to default layout
//            mMusicAdapter.notifyItemChanged(mCurrentPosition);
//            mCurrentPosition = -1;
//            isDownloading = false;
//            downloadThemeId = -1;
//        }
    }

    public void pauseDownload() {
//        if (Status.RUNNING == PRDownloader.getStatus(downloadThemeId)
//                || Status.QUEUED == PRDownloader.getStatus(downloadThemeId)) {
//            PRDownloader.pause(downloadThemeId);
//        }
    }

    public void resumeDownload() {
//        if (Status.PAUSED == PRDownloader.getStatus(downloadThemeId)) {
//            PRDownloader.resume(downloadThemeId);
//        }
    }

    @Override
    public void onReturnPosition(int position) {
        mCurrentPosition = position;
    }

    /**
     * Public method for pause Music when Tab Changed
     */
    public void releaseMusic() {
        if (mCurrentPosition != -1) {
            scrollToItem(mCurrentPosition);
            postDelay(() -> {
                if (mRecycler.findViewHolderForAdapterPosition(mCurrentPosition) != null) {
                    MusicOnlineAdapter.ItemViewHolder holder =
                            (MusicOnlineAdapter.ItemViewHolder) mRecycler.findViewHolderForAdapterPosition(mCurrentPosition);
                    if (holder != null) holder.hideItem();
                }
            });
        }
    }

    @Override
    public void onPause() {
//        Lo.d(TAG, "IS PAUSE AUDIO: " + isPauseAudio);
//        isPauseAudio = true;
        if (mCurrentPosition != -1) {
            scrollToItem(mCurrentPosition);
            postDelay(() -> {
                if (mRecycler.findViewHolderForAdapterPosition(mCurrentPosition) != null) {
                    MusicOnlineAdapter.ItemViewHolder holder =
                            (MusicOnlineAdapter.ItemViewHolder) mRecycler.findViewHolderForAdapterPosition(mCurrentPosition);
                    if (holder != null) holder.pausePreview();
                }
            });
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
//        isPauseAudio = false;
    }

    @Override
    public void onDestroy() {
//        LogUtils.d(TAG, "onDestroy() ");
        if (mMusicAdapter != null) {
            mMusicAdapter.releaseMusicPreview();
        }
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        if (mMusicAdapter != null) {
            mMusicAdapter.destroyAd();
        }
        super.onDetach();
    }

    private int downloadThemeId = -1;

    /**
     *
     * @param audioUrl
     * @param targetName
     */
    private void downLoadTheme(String audioUrl, String targetName) {
//        if (Status.RUNNING == PRDownloader.getStatus(downloadThemeId)) {
//            PRDownloader.pause(downloadThemeId);
//            return;
//        }
//        if (Status.PAUSED == PRDownloader.getStatus(downloadThemeId)) {
//            PRDownloader.resume(downloadThemeId);
//            return;
//        }

        isDownloading = true;

        mCustomProgressBar.show(requireActivity());
        String outAudioFolder = GlobalDef.FOLDER_AUDIO;
        FunctionUtils.createFolder(outAudioFolder);

        ManagerStorage.downloadFileToExternalStorage(GlobalDef.PATH_DOWNLOAD_AUDIO + audioUrl,
                outAudioFolder,
                targetName,
                new OnDownloadFileListener() {
                    @Override
                    public void OnSuccessListener(@NotNull File file) {
                        isDownloading = false;
                        downloadThemeId = -1;

                        if (getActivity() != null && !getActivity().isFinishing()) {
                            // DOWNLOAD SUCCESS
                            if (mCurrentPosition != -1 && mCurrentPosition < mMusics.size()) {
                                mMusics.get(mCurrentPosition).setDownloaded(true);
                                mMusics.get(mCurrentPosition).setLoading(false);
                            }
                            // Notify download success and play audio
                            scrollToItem(mCurrentPosition);
                            postDelay(() -> {
                                if (mRecycler.findViewHolderForAdapterPosition(mCurrentPosition) != null) {
                                    MusicOnlineAdapter.ItemViewHolder holder =
                                            (MusicOnlineAdapter.ItemViewHolder) mRecycler.findViewHolderForAdapterPosition(mCurrentPosition);
                                    if (holder != null && isAdded() && isVisible() && getUserVisibleHint()) {
                                        // when Activity is live
                                        mMusics.get(mCurrentPosition).setSelected(true);
                                        holder.startPreviewMusic(0, mMusics.get(mCurrentPosition).getDuration());
                                    } else if (holder != null) {
                                        mMusics.get(mCurrentPosition).setSelected(false);
                                        holder.hideItem();
                                    }
                                }

                                // Notify Data
                                mMusicAdapter.notifyItemChanged(mCurrentPosition);
                                // Save Data
                                RealmUtil.getInstance().saveData(mMusics.get(mCurrentPosition));
                                // Reset index if audio download not play
                                if (!mMusics.get(mCurrentPosition).isSelected()) {
                                    mCurrentPosition = -1;
                                }
                            });
                        }

                        mCustomProgressBar.dialog.dismiss();
                    }

                    @Override
                    public void OnFailListener() {
                        isDownloading = false;
                        downloadThemeId = -1;

                        // Set Music model to default like not yet download
                        if (mCurrentPosition != -1 && mCurrentPosition < mMusics.size()) {
                            mMusics.get(mCurrentPosition).setDownloaded(false);
                            mMusics.get(mCurrentPosition).setSelected(false);
                            mMusics.get(mCurrentPosition).setLoading(false);
                        }
                        // Notify item still download to default layout
                        mMusicAdapter.notifyItemChanged(mCurrentPosition);
                        mCurrentPosition = -1;
                        // Show error
//                        To.show(R.string.message_network_not_available);

                        mCustomProgressBar.dialog.dismiss();
                    }
                });
    }

    /**
     *
     * @param pos
     */
    private void scrollToItem(int pos) {
        // Must be scroll first because for findViewHolderForAdapterPosition not return null
        mRecycler.getLayoutManager().scrollToPosition(pos);
    }

    /**
     *
     * @param handler
     */
    private void postDelay(ThreadUtils.IHandler handler) {
        mRecycler.postDelayed(handler::onWork, 100);
    }

    // ====================== FIREBASE ========================
    private void getDataAudioFromFBS() {
        if (getValueCacheAudio() == 1) {
            requestAudioFromServer();
        } else {
            setValueCacheAudio(getValueCacheAudio() - 1);
            loadDataFromCache();
        }
    }

    private int getValueCacheAudio() {
        return SharePrefUtils.getInt(GlobalDef.SHARF_RELOAD_LIST_AUDIO, 1);
    }

    private void setValueCacheAudio(int count) {
        SharePrefUtils.putInt(GlobalDef.SHARF_RELOAD_LIST_AUDIO, count);
    }
}