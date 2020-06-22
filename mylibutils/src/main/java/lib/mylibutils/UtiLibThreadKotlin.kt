package lib.mylibutils

import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import java.util.*

class UtiLibThreadKotlin private constructor() {
    //----------------------------------------------------------------------------------------------
    private var listTasks: MutableList<AsyncTask<*, *, *>>?

    //----------------------------------------------------------------------------------------------
    fun runBackground(listener: IBackground?): DoJobBackground {
        val executor = DoJobBackground()
        executor.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, listener)
        listTasks!!.add(executor) // Cached
        return executor
    }

    fun removeAllBackgroundThreads() {
        if (listTasks != null && !listTasks!!.isEmpty()) {
            for (task in listTasks!!) {
                if (!task.isCancelled && task.status == AsyncTask.Status.RUNNING) { // Lo.d("PickImageExtendsActivity", "removeAllBackgroundThreads");
                    task.cancel(true)
                }
            }
            listTasks!!.clear()
        }
        listTasks = null
        utiLibThreadKotlin = null
    }

    //----------------------------------------------------------------------------------------------
    fun runOnUiMessage(handler: IHandler?): Handler {
        val mIncomingHandler =
            Handler(Handler.Callback { _: Message? ->
                handler?.onWork()
                true
            })
        mIncomingHandler.sendEmptyMessage(0)
        return mIncomingHandler
    }

    fun runOnUiMessageDelay(handler: IHandler?, delay: Long): Handler {
        val mIncomingHandler =
            Handler(Handler.Callback { _: Message? ->
                handler?.onWork()
                true
            })
        mIncomingHandler.sendEmptyMessageDelayed(0, delay)
        return mIncomingHandler
    }

    /**
     * Gửi 1 message, sẽ gọi đến method doWork của IHandler
     *
     * @param mIHandler
     */
    fun runOnUI(mIHandler: IHandler?): Handler {
        val handler = Handler()
        handler.post { mIHandler?.onWork() }
        return handler
    }
    //----------------------------------------------------------------------------------------------
    /**
     * Thực hiện 1 mIHandler sau 1 khoảng thời gian timeDelay
     *
     * @param mIHandler
     * @param timeDelay
     */
    fun runUIDelay(mIHandler: IHandler?, timeDelay: Int): Handler {
        val handler = Handler()
        handler.postDelayed({ mIHandler?.onWork() }, timeDelay.toLong())
        return handler
    }

    // ================= INTERFACE ===================
    interface IHandler {
        fun onWork()
    }

    interface IHandlerData<E> {
        fun onWork(data: E)
    }

    interface IHandlerExtra : IHandler {
        fun onLoadFailed()
    }

    interface IBackground {
        fun doingBackground()
        fun onCompleted()
        fun onCancel()
    }

    class DoJobBackground :
        AsyncTask<IBackground?, Void?, Void?>() {
        private var mListener: IBackground? = null

        override fun onCancelled() {
            super.onCancelled()
            if (mListener != null) {
                mListener!!.onCancel()
            }
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            if (mListener != null) {
                mListener!!.onCompleted()
            }
        }

        override fun doInBackground(vararg params: IBackground?): Void? {
            mListener = params[0]
            if (mListener != null) {
                mListener!!.doingBackground()
            }
            return null
        }
    }

    companion object {
        private var utiLibThreadKotlin: UtiLibThreadKotlin? = null
        val instance: UtiLibThreadKotlin?
            get() {
                if (utiLibThreadKotlin == null) {
                    utiLibThreadKotlin = UtiLibThreadKotlin()
                }
                return utiLibThreadKotlin
            }
    }

    init { // cache list task for cancellable
        listTasks = ArrayList()
    }
}