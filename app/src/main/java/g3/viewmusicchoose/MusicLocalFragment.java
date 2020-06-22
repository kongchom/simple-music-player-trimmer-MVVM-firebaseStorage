package g3.viewmusicchoose;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MusicLocalFragment extends Fragment implements MusicAdapter.OnClickItemMusicLocalListener, View.OnClickListener {
    private static final String KEY_NAME_TAB = "tab";
    private static final int REQUEST_CODE_PICK_MUSIC = 100;
    private static final String TAG = "MusicLocalFragment";

    private RecyclerView mRecycler;
    private MusicAdapter mMusicAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<LocalSong> mMusics = new ArrayList<>();
    private OnReceiveMusicListener listener;
    private Button mBtnPickMusic;
    private int mCurrentPosition = -1;

    private Activity mAct;


    public static MusicLocalFragment initialize(Activity act, String text, OnReceiveMusicListener listener) {
        MusicLocalFragment fragment = new MusicLocalFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        mRecycler = (RecyclerView) view.findViewById(R.id.recycler);
        mBtnPickMusic = (Button) view.findViewById(R.id.btnAddMusic);
        mLayoutManager = new LinearLayoutManager(mAct);
        mMusicAdapter = new MusicAdapter(mAct, mMusics);

        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mMusicAdapter);
        mMusicAdapter.setOnClickItemMusicListener(this);
        mBtnPickMusic.setOnClickListener(this);

        getDataMusicLocal();
        return view;
    }

    @Override
    public void onDestroy() {
        mMusicAdapter.releaseMusicPreview();
        super.onDestroy();
    }

    public void getDataMusicLocal() {
        List<LocalSong> bindAllSongs = MusicUtils.bindAllSongs(mAct);
        if (bindAllSongs != null) {
            mMusics.addAll(bindAllSongs);
            if (mMusicAdapter!= null) {
                mMusicAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClickItem(int position, int timeStart, int timeEnd) {
        if (mMusics != null && position < mMusics.size()) {
            LocalSong song = mMusics.get(position);
            if (song != null && listener != null) {
                listener.receive(song.getSongTitle(), song.getDuration(), song.getSongData(), timeStart, timeEnd);
            } else {
//                To.show(R.string.msg_music_pick_audio_failed);
            }
        } else {
//            To.show(R.string.msg_music_pick_audio_failed);
        }
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
                    MusicAdapter.ItemViewHolder holder = (MusicAdapter.ItemViewHolder) mRecycler.findViewHolderForAdapterPosition(mCurrentPosition);
//                    mMusicAdapter.ItemViewHolder holder =
//                            (mMusicAdapter.ItemViewHolder) mRecycler.findViewHolderForAdapterPosition(mCurrentPosition);
                    if (holder != null) holder.hideItem();
                }
            });
        }
    }

    @Override
    public void onPause() {
        if (mCurrentPosition != -1) {
            scrollToItem(mCurrentPosition);
            postDelay(() -> {
                if (mRecycler.findViewHolderForAdapterPosition(mCurrentPosition) != null) {
                    MusicAdapter.ItemViewHolder holder = (MusicAdapter.ItemViewHolder) mRecycler.findViewHolderForAdapterPosition(mCurrentPosition);
//                    mMusicAdapter.ItemViewHolder holder =
//                            (mMusicAdapter.ItemViewHolder) mRecycler.findViewHolderForAdapterPosition(mCurrentPosition);
                    if (holder != null) holder.pausePreview();
                }
            });
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        // Pause Music
        if (mCurrentPosition != -1) {
            scrollToItem(mCurrentPosition);
            mRecycler.postDelayed(() -> {
                if (mRecycler.findViewHolderForAdapterPosition(mCurrentPosition) != null) {
                    MusicAdapter.ItemViewHolder holder = (MusicAdapter.ItemViewHolder) mRecycler.findViewHolderForAdapterPosition(mCurrentPosition);
                    if (holder != null) {
                        holder.stopPreview();
                        holder.hideItem();
                    }
                }
                mMusics.get(mCurrentPosition).setSelect(false);
            }, 100);

        }

        // Pick Music from Intent
        pickAudioFromIntent();

    }

    private void pickAudioFromIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "audio/*");
        startActivityForResult(Intent.createChooser(intent, "text_select_music"), REQUEST_CODE_PICK_MUSIC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_MUSIC) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
//                LocalSong song = MusicUtils.convertUriToSong(getActivity(), uri);
                if (uri != null) {
//                    Lo.d(TAG, "Pick Audio: " + uri.toString());
                    final LocalSong song = getAudioFromUri(uri);
                    if (song != null) {
                        checkAndAddSongFromChooser(song);
                    } else {
//                        To.show(R.string.msg_music_pick_audio_failed);
                    }
                } else {
//                    To.show(R.string.msg_music_pick_audio_failed);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkAndAddSongFromChooser(LocalSong localSong) {
        if (mMusics != null) {
            // If old song play Music
            int size = mMusics.size();
            for (int i = 0; i < size; i++) {
                LocalSong audio = mMusics.get(i);
                if (localSong.getSongData().equals(audio.getSongData()) ||
                        localSong.getSongTitle().equals(audio.getSongTitle())) {


//                    Lo.d(TAG, "LOCAL_SONG: " + audio.getSongTitle());
                    // Must be scroll first because for findViewHolderForAdapterPosition not return null
                    scrollToItem(i);

                    // Play song available
                    int finalI = i;
                    postDelay(() -> {
                        if (mRecycler != null) {
                            // Find holder and play music
                            if (mRecycler.findViewHolderForAdapterPosition(finalI) != null) {
                                MusicAdapter.ItemViewHolder holder = (MusicAdapter.ItemViewHolder) mRecycler.findViewHolderForAdapterPosition(finalI);
                                if (holder != null) {
                                    holder.onClickViewItem(finalI);
                                    mCurrentPosition = finalI;
                                }
                            } else {
//                                Lo.d(TAG, "No Fill INDEX: " + finalI);
                            }
                        }
                    });
                    return;
                }
            }

            // Add NEWS
            mMusics.add(0, localSong);
            mMusicAdapter.notifyDataSetChanged();
            scrollToItem(0);
            mCurrentPosition = 0;
            // Play
            postDelay(() -> {
                if (mRecycler.findViewHolderForAdapterPosition(mCurrentPosition) != null) {
                    MusicAdapter.ItemViewHolder holder =
                            (MusicAdapter.ItemViewHolder) mRecycler.findViewHolderForAdapterPosition(0);
                    if (holder != null) holder.onClickViewItem(0);
                }
            });
        }
    }

    private void postDelay(ThreadUtils.IHandler handler) {
        mRecycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.onWork();
            }
        }, 100);
    }

    private void scrollToItem(int pos) {
        // Must be scroll first because for findViewHolderForAdapterPosition not return null
        mRecycler.getLayoutManager().scrollToPosition(pos);
    }

    private LocalSong getAudioFromUri(Uri uri) {
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };
        if (getActivity() == null || getActivity().isFinishing()) return null;

        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            int indexName = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int indexDuration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int indexArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            if (cursor.moveToFirst()) {
                String pathFile = FileUtils.getPath(getActivity(), uri);

                String name = indexName != -1 ? cursor.getString(indexName) : "Unknown name";
                int duration = indexDuration != -1 ? cursor.getInt(indexDuration) : 0;// Milliseconds
                String artist = indexArtist != -1 ? cursor.getString(indexArtist) : "Unknown artist";
                if (!FunctionUtils.isBlank(pathFile)) {
                    File file = new File(pathFile);
                    if (file.exists()) {
                        LocalSong GSC = new LocalSong();

                        String audioName = pathFile.substring(pathFile.lastIndexOf(File.separator) + 1);

                        // See: FileUtils#savefileFromUri
                        if (FunctionUtils.isBlank(audioName) || audioName.equals("tmp.mp3")) {
                            audioName = name;
                        }
                        GSC.setSongTitle(audioName);
                        GSC.setSongArtist(artist);
                        GSC.setSongData(pathFile);

                        if (duration != 0) {
                            String timeMusic = String.valueOf(duration);
                            GSC.setTime(timeMusic);
                        } else {
                            if (!FunctionUtils.isBlank(pathFile)) {
                                try {
                                    GSC.setTime(String.valueOf(VideoUtils.getDurationVideo(pathFile)));
                                } catch (IllegalArgumentException ex) {
                                    ex.printStackTrace();
                                    GSC.setTime("0");
                                }
                            } else {
                                GSC.setTime("0");
                            }
                        }
                        // Scan file for show Audio local
                        FunctionUtils.scanFile(getActivity(), pathFile);
                        return GSC;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
            // Close cursor
            cursor.close();
        }
        return null;
    }

    @Override
    public void onDetach() {
//        if (mMusicAdapter != null) {
//            mMusicAdapter.destroyAd();
//        }
        super.onDetach();
    }
}
