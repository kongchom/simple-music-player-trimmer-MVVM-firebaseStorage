package g3.viewmusicchoose;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ThemeUtils {
    /**
     * Get list audio from Assets folder
     */
    public static List<Music> getListMusicDefault(Context context) {
        List<Music> listMusicDefault = new ArrayList<>();
        try {
            String[] list = context.getAssets().list("music");
            if (list != null) {
                for (String fileName : list) {
                    if (fileName.endsWith(".mp3")) {
                        String audioPath = GlobalDef.FOLDER_AUDIO + fileName;
                        File file = new File(audioPath);
                        if (file.exists()) {
                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(file.getAbsolutePath());
                            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            Music musicLocal = new Music();
                            musicLocal.setAssetAudioFile(true);
                            musicLocal.setDuration(Integer.parseInt(duration) / 1000);
                            musicLocal.setUrl(audioPath);
                            musicLocal.setName(fileName);
                            musicLocal.setDownload(true);
                            musicLocal.setSelect(false);
                            listMusicDefault.add(musicLocal);
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listMusicDefault;
    }

}
