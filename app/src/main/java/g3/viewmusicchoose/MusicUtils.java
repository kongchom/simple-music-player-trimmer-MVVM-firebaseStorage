package g3.viewmusicchoose;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class MusicUtils {
    public static List<LocalSong> bindAllSongs(Context context) {
        Log.e("MusicUtils","context = " + context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        List<LocalSong> localSongs = new ArrayList<>();

        /** Making custom drawable */
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0" +
                " And " + MediaStore.Audio.Media.DISPLAY_NAME + " like ? ";
        final String[] projection = new String[]{
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION};
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE
                + " COLLATE LOCALIZED ASC";

        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;// chọn bộ nhớ ngoài
        Cursor cursor = context.getContentResolver().query(uri,
                projection, selection, new String[]{"%mp3%"}, sortOrder);       // lấy con trỏ thao tác trên tệp dữ liệu chỉ nhận tệp mp3

        try {
            // the uri of the table that we want to query
            // query the db
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    LocalSong GSC = new LocalSong();            //bind data vào list<LocalSong>
                    GSC.setSongTitle(cursor.getString(0));
                    GSC.setSongArtist(cursor.getString(1));
                    String fileMusic = cursor.getString(2);
                    GSC.setSongData(fileMusic);
                    String timeMusic = cursor.getString(3);
                    if (timeMusic != null && !timeMusic.isEmpty()) {
                        GSC.setTime(timeMusic);
                    } else {
                        if (FunctionUtils.isBlank(fileMusic)) {
                            GSC.setTime(String.valueOf(VideoUtils.getDurationVideo(fileMusic)));
                        } else {
                            GSC.setTime("0");
                        }
                    }
                    localSongs.add(GSC);
                    cursor.moveToNext();
                }
                return localSongs;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}
