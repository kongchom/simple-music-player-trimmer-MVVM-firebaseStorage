package g3.viewmusicchoose.util;

import android.content.Context;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

import javax.annotation.Nullable;

import g3.viewmusicchoose.BuildConfig;
import g3.viewmusicchoose.GlobalDef;

public final class MyMediaPlayer {

    public final static String APP_RAW_URI_PATH_1 = String.format("android.resource://%s/raw/", BuildConfig.APPLICATION_ID);

    private static final String TAG = "MyMediaPlayer";

    private static volatile MyMediaPlayer instance = null;
    MediaPlayer mp;
    private Context context;

    private MyMediaPlayer(Context context) {
        this.context = context;
    }

    public static MyMediaPlayer getInstance(Context context) {
        if (instance == null) {
            synchronized (MyMediaPlayer.class) {
                if (instance == null) {
                    instance = new MyMediaPlayer(context);
                }
            }
        }
        return instance;
    }

    /**
     *
     * @param fileName if sound name is "sound.mp3" then pass fileName as "sound" only.
     */
    public synchronized void playSound(String fileName, @Nullable  String dataSource) {
        
        if (instance.mp == null) {
            instance.mp = new MediaPlayer();
        } else {
            instance.mp.reset();
        }
        try {
            if (!fileName.isEmpty()) {
                instance.mp.setDataSource(context, Uri.parse(GlobalDef.FOLDER_AUDIO + fileName));
            }
            if (dataSource != null) {
                instance.mp.setDataSource(dataSource);
            }
            instance.mp.prepare();
            instance.mp.setVolume(100f, 100f);
            instance.mp.setLooping(false);
            instance.mp.start();
            instance.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (instance.mp != null) {
                        instance.mp.reset();
                        instance.mp = null;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized  Boolean getPlayingState() {
        return instance.mp.isPlaying();
    }

    public synchronized Boolean checkNotNull() {
        return instance.mp != null;
    }

    public synchronized void stopSound() {
        if (instance.mp != null) {
            instance.mp.stop();
            instance.mp.release();
        }
    }

    public synchronized void pauseSound() {
        if (instance.mp != null) {
            instance.mp.pause();
        }
    }

    public synchronized void restartSound() {
        if (instance.mp != null) {
            instance.mp.start();
        }
    }

    public synchronized void playRepeatedSound(String fileName) {
        if (instance.mp == null) {
            instance.mp = new MediaPlayer();
        } else {
            instance.mp.reset();
        }
        try {
            instance.mp.setDataSource(context, Uri.parse(APP_RAW_URI_PATH_1 + fileName));
            instance.mp.prepare();
            instance.mp.setVolume(100f, 100f);
            instance.mp.setLooping(true);
            instance.mp.start();
            instance.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mp != null) {
                        mp.reset();
                        mp = null;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}