package lib.mylibutils;

import android.util.Log;

public class MyLog {
    private static String TAG = "MY_DEBUG_";
    private static boolean DEBUG = true;

    public static boolean isDEBUG() {
        return DEBUG;
    }

    public static final int TEST = 0;
    public static final int RELEASE = 1;

    public static void setDebug(boolean isRelease) {
        MyLog.DEBUG = isRelease;
    }

    public static String getTAG() {
        return TAG;
    }

    public static void setTAG(String TAG) {
        MyLog.TAG = TAG;
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, "" + msg);
        }
    }

    public static void d(String msg) {
        if (DEBUG) {
            Log.d(TAG, "" + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, "" + msg);
        }
    }

    public static void i(String msg) {
        if (DEBUG) {
            Log.i(TAG, "" + msg);
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, "" + msg);
        }
    }

    public static void v(String msg) {
        if (DEBUG) {
            Log.v(TAG, "" + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, "" + msg);
        }
    }

    public static void w(String msg) {
        if (DEBUG) {
            Log.w(TAG, "" + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, "" + msg);
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            Log.e(TAG, "" + msg);
        }
    }
}