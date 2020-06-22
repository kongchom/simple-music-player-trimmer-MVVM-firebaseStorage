package g3.viewmusicchoose;

import android.media.MediaMetadataRetriever;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class VideoUtils {
    public static long getDurationVideo(String videoUri) {
        MediaMetadataRetriever mediaMetadataRetriever = null;
        FileInputStream inputStream = null;
        long timeInMillisec = 0;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            inputStream = new FileInputStream(videoUri);
            mediaMetadataRetriever.setDataSource(inputStream.getFD());
            String time = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            timeInMillisec = Long.parseLong(time);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return timeInMillisec;
    }
}
