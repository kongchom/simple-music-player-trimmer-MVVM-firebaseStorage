package g3.viewmusicchoose;

import android.os.AsyncTask;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class ThreadUtils {
    private static ThreadUtils ThreadUtils;

    public static ThreadUtils getInstance() {
        if (ThreadUtils == null) {
            ThreadUtils = new ThreadUtils();
        }

        return ThreadUtils;
    }

    //----------------------------------------------------------------------------------------------
    private List<AsyncTask> listTasks;

    private ThreadUtils() {
        // cache list task for cancellable
        listTasks = new ArrayList<>();
    }


    //----------------------------------------------------------------------------------------------
    public DoJobBackground runBackground(IBackground listener) {
        DoJobBackground executor = new DoJobBackground();
        executor.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, listener);
        listTasks.add(executor);// Cached
        return executor;
    }

    public void removeAllBackgroundThreads() {
        if (listTasks != null && !listTasks.isEmpty()) {
            for (AsyncTask task : listTasks) {
                if (!task.isCancelled() && task.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    // Lo.d("PickImageExtendsActivity", "removeAllBackgroundThreads");
                    task.cancel(true);
                }
            }
            listTasks.clear();
        }
        listTasks = null;
        ThreadUtils = null;
    }

    //----------------------------------------------------------------------------------------------

    public Handler runOnUiMessage(final IHandler handler) {
        Handler mIncomingHandler = new Handler(msg -> {
            if (handler != null) {
                handler.onWork();
            }
            return true;
        });

        mIncomingHandler.sendEmptyMessage(0);
        return mIncomingHandler;
    }

    public Handler runOnUiMessageDelay(final IHandler handler, long delay) {
        Handler mIncomingHandler = new Handler(msg -> {
            if (handler != null) {
                handler.onWork();
            }
            return true;
        });

        mIncomingHandler.sendEmptyMessageDelayed(0, delay);
        return mIncomingHandler;
    }

    /**
     * Gửi 1 message, sẽ gọi đến method doWork của IHandler
     *
     * @param mIHandler
     */
    public Handler runOnUI(final IHandler mIHandler) {
        Handler handler = new Handler();
        handler.post(() -> {
            if (mIHandler != null) {
                mIHandler.onWork();
            }
        });
        return handler;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Thực hiện 1 mIHandler sau 1 khoảng thời gian timeDelay
     *
     * @param mIHandler
     * @param timeDelay
     */
    public Handler runUIDelay(final IHandler mIHandler, final int timeDelay) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (mIHandler != null) {
                mIHandler.onWork();
            }
        }, timeDelay);
        return handler;
    }

    // ================= INTERFACE ===================

    public interface IHandler {
        void onWork();
    }

    public interface IHandlerData<E> {
        void onWork(E data);
    }

    public interface IHandlerExtra extends IHandler {
        void onLoadFailed();
    }

    public interface IBackground {
        void doingBackground();

        void onCompleted();

        void onCancel();
    }

    public static class DoJobBackground extends AsyncTask<IBackground, Void, Void> {

        private IBackground mListener;

        @Override
        protected Void doInBackground(IBackground... params) {
            mListener = params[0];
            if (mListener != null) {
                mListener.doingBackground();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mListener != null) {
                mListener.onCancel();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mListener != null) {
                mListener.onCompleted();
            }
        }

    }
}
