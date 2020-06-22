package lib.mylibutils.customactivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public abstract class BaseThreadActivity extends AppCompatActivity {
    private static final String TAG = BaseThreadActivity.class.getSimpleName();
    private Handler handler;
    private HandlerThread handlerThread;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(null);
    }

    @Override
    public synchronized void onStart() {
        Log.d(TAG, "onStart " + this);
        super.onStart();
    }

    @Override
    public synchronized void onResume() {
        Log.d(TAG, "onResume " + this);
        super.onResume();

        handlerThread = new HandlerThread("inference");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public synchronized void onPause() {
        Log.d(TAG, "onPause " + this);

        handlerThread.quitSafely();
        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (final InterruptedException e) {
            Log.e(TAG, "Exception!" + e);
        }

        super.onPause();
    }

    @Override
    public synchronized void onStop() {
        Log.d(TAG, "onStop " + this);
        super.onStop();
    }

    @Override
    public synchronized void onDestroy() {
        Log.d(TAG, "onDestroy " + this);
        super.onDestroy();
    }

    protected synchronized void runInBackground(final Runnable r) {
        if (handler != null) {
            handler.post(r);
        }
    }

    public void runInBackground(final Runnable runInBackground, final Runnable runCompleted, final View layoutLoading) {
        if (layoutLoading != null)
            layoutLoading.setVisibility(View.VISIBLE);
        runInBackground(new Runnable() {
            @Override
            public void run() {
                runInBackground.run();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (layoutLoading != null)
                            layoutLoading.setVisibility(View.GONE);
                        if (runCompleted != null)
                            runCompleted.run();
                    }
                });
            }
        });
    }
}


