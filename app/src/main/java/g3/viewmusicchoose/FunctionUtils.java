package g3.viewmusicchoose;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;

import java.io.File;

public class FunctionUtils {

    public static boolean haveNetworkConnection(Context ctx) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
            return haveConnectedWifi || haveConnectedMobile;
        } catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }
    }

    /**
     * Tests if a string is blank: null, empty, or only whitespace (" ", \r\n,
     * \t, etc)
     *
     * @param string A value input
     * @return true if string is blank
     */
    public static boolean isBlank(String string) {
        if (string == null || string.length() == 0) {
            return true;
        }
        int l = string.length();
        for (int i = 0; i < l; i++) {
            if (!Character.isWhitespace(string.codePointAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static void createFolder(String dir) {
        File folder = new File(dir);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static void scanFile(Context contexts, String pathFile) {
        scanFile(contexts, new String[]{pathFile});
    }

    public static void scanFile(Context contexts, String[] paths) {
        MediaScannerConnection.scanFile(contexts,
                paths,
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        // scanned path and uri
//                        Lo.d("SCAN MEDIA FILE", "SCAN MEDIA COMPLETED: " + path);
                    }
                });
    }

    public static void openAppSettings(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        context.startActivity(intent);
    }

}
