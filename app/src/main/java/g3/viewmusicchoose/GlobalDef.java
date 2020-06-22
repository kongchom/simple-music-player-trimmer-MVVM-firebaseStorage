package g3.viewmusicchoose;

import android.os.Environment;

public class GlobalDef {

    public static final String OUTPUT_FOLDER_NAME = "VideoMakerSlideshow";
    public final static String DEFAULT_FOLDER_OUTPUT = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + OUTPUT_FOLDER_NAME;
    public static final String FOLDER_AUDIO = DEFAULT_FOLDER_OUTPUT + "/.Audio/";

    public static final String SHARF_RELOAD_LIST_AUDIO = "RELOAD_LIST_AUDIO";

    public static final int VALUE_CACHED_RELOAD_LIST_THEME = 5;

    public final static String PATH_DOWNLOAD_AUDIO = "/Video/Audio/";
    public static final String FB_URL_VIDEO_MAKER_AUDIO = "/API/VideoMaker/video_maker_audio.json";
}
