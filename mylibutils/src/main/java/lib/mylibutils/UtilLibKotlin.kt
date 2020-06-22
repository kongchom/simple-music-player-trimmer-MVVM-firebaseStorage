@file:JvmName("UtilLibs")

package lib.mylibutils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class UtilLibKotlin {

    companion object {
        @JvmStatic
        fun handlerDoWork(mIHandler: UtiLibThreadKotlin.IHandler?) {
            var mHandler = Handler(Handler.Callback { msg ->
                val mIHandler: UtiLibThreadKotlin.IHandler = msg.obj as UtiLibThreadKotlin.IHandler
                mIHandler.onWork()
                true
            })

            val message: Message = mHandler.obtainMessage(0, mIHandler)
            mHandler.sendMessage(message)
        }

        @JvmStatic
        fun isBlank(string: String?): Boolean {
            if (string == null || string.isEmpty()) {
                return true
            }
            val l = string.length
            for (i in 0 until l) {
                if (!Character.isWhitespace(string.codePointAt(i))) {
                    return false
                }
            }
            return true
        }

        //==============================================================================================
        const val REQUEST_CODE_ASK_ALL_PERMISSIONS = 10001
        fun getCurrentSdkVersion(): Int {
            return Build.VERSION.SDK_INT
        }

        //----------------------------------------------------------------------------------------------
        @TargetApi(Build.VERSION_CODES.M)
        fun addPermission(
                activity: Activity?,
                permissionsList: MutableList<String?>,
                permission: String?
        ): Boolean {
            if (ContextCompat.checkSelfPermission(
                            activity!!,
                            permission!!
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsList.add(permission)
                // User click not show again
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    return false
                }
            }
            return true
        }

        //----------------------------------------------------------------------------------------------
        //call when is start app for first (Gọi lần đầu tiên khi chạy app)
        @TargetApi(Build.VERSION_CODES.M)
        fun requestAllPermission(mActivity: Activity?, permissionsNeeded: List<String>): Boolean {
            if (getCurrentSdkVersion() >= 23) {
                val permissionsList: MutableList<String?> = ArrayList()
                for (i in permissionsNeeded.indices) {
                    if (!addPermission(mActivity, permissionsList, permissionsNeeded[i])) {
                        permissionsList.add(permissionsNeeded[i])
                    }
                }
                if (permissionsList.size > 0) {
                    ActivityCompat.requestPermissions(
                            mActivity!!,
                            permissionsList.toTypedArray(),
                            REQUEST_CODE_ASK_ALL_PERMISSIONS
                    )
                    return false
                }
            }
            return true
        }

        //----------------------------------------------------------------------------------------------
        @TargetApi(Build.VERSION_CODES.M)
        @JvmStatic
        fun requestPermission(mActivity: Activity?, permission: String, REQUEST_CODE: Int) {
            if (getCurrentSdkVersion() >= 23) {
                val permissionsNeeded: MutableList<String> = ArrayList()
                permissionsNeeded.add(permission)
                ActivityCompat.requestPermissions(
                        mActivity!!,
                        permissionsNeeded.toTypedArray(),
                        REQUEST_CODE
                )
            }
        }

        //----------------------------------------------------------------------------------------------
        @TargetApi(Build.VERSION_CODES.M)
        @JvmStatic
        fun isPermissionAllow(activity: Activity?, permission: String?): Boolean {
            return if (getCurrentSdkVersion() < 23) true else ContextCompat.checkSelfPermission(
                    activity!!,
                    permission!!
            ) == PackageManager.PERMISSION_GRANTED
        }

        //----------------------------------------------------------------------------------------------
        @JvmStatic
        fun isPermissionAllow(context: Context?, requestCode: Int, permission: String?): Boolean {
            if (isPermissionAllow(context as Activity?, permission)) return true
            if (permission != null) {
                requestPermission(context, permission, requestCode)
            }
            return false
        }

        //----------------------------------------------------------------------------------------------
        fun showDenyDialog(
                activity: Activity,
                onYes: DialogInterface.OnClickListener?, isFinishActivity: Boolean
        ) {
            showDenyDialog(
                    activity,
                    onYes,
                    DialogInterface.OnClickListener { dialog, which -> if (isFinishActivity) activity.finish() })
        }

        //----------------------------------------------------------------------------------------------
        fun showRememberDialog(activity: Activity, isFinishActivity: Boolean) {
            showRememberDialog(
                    activity,
                    DialogInterface.OnClickListener { dialog, which ->
                        openAppSettings(
                                activity,
                                activity.packageName
                        )
                    },
                    DialogInterface.OnClickListener { dialog, which -> if (isFinishActivity) activity.finish() })
        }

        //----------------------------------------------------------------------------------------------
        @JvmStatic
        fun showDenyDialog(
                activity: Activity?,
                onRetry: DialogInterface.OnClickListener?,
                onCancel: DialogInterface.OnClickListener?
        ) {
            CommonDialog.showDialogConfirm(
                    activity, R.string.message_permission_denied,
                    "Retry", "Cancel", onRetry, onCancel
            )
        }

        //----------------------------------------------------------------------------------------------
        @JvmStatic
        fun showRememberDialog(
                activity: Activity?,
                onSettings: DialogInterface.OnClickListener?,
                onCancel: DialogInterface.OnClickListener?
        ) {
            CommonDialog.showDialogConfirm(
                    activity, R.string.message_permission_denied_remember,
                    "Settings", "Cancel", onSettings, onCancel
            )
        }

        //----------------------------------------------------------------------------------------------
        @JvmStatic
        fun openAppSettings(context: Context, isFinishActivity: Boolean) {
            showRememberDialog(context as Activity, DialogInterface.OnClickListener { dialog,
                                                                                      which ->
                openAppSettings(context, context.getPackageName())
            }, DialogInterface.OnClickListener { dialog,
                                                 which ->
                if (isFinishActivity)
                    context.finish()
            })
        }

        @JvmStatic
        fun openAppSettings(
                context: Context,
                isFinishActivity: Boolean,
                isFinishActivityWhenClickSetting: Boolean
        ) {
            showRememberDialog(context as Activity, DialogInterface.OnClickListener { dialog,
                                                                                      which ->
                openAppSettings(context, context.getPackageName())
                if (isFinishActivityWhenClickSetting)
                    context.finish()
            }, DialogInterface.OnClickListener { dialog,
                                                 which ->
                if (isFinishActivity)
                    context.finish()
            })
        }

        fun openAppSettings(context: Context, packageName: String?) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            context.startActivity(intent)
        }

        //----------------------------------------------------------------------------------------------
        @JvmStatic
        fun showDenyDialog(
                context: Context,
                permission: String?,
                requestCode: Int,
                isFinishActivity: Boolean
        ) {
            showDenyDialog(
                    (context as Activity),
                    DialogInterface.OnClickListener { dialog, which ->
                        if (permission != null) {
                            requestPermission(context, permission, requestCode)
                        }
                    },
                    DialogInterface.OnClickListener { dialog, which -> if (isFinishActivity) context.finish() })
        }

        @JvmStatic
        fun showDenyDialog(
                context: Context,
                permission: String?,
                requestCode: Int,
                isFinishActivity: Boolean,
                isFinishActivityWhenClickSetting: Boolean
        ) {
            showDenyDialog(
                    (context as Activity),
                    DialogInterface.OnClickListener { dialog, which ->
                        if (permission != null) {
                            requestPermission(context, permission, requestCode)
                            if (isFinishActivityWhenClickSetting)
                                context.finish()
                        }
                    },
                    DialogInterface.OnClickListener { dialog, which -> if (isFinishActivity) context.finish() })
        }

        //----------------------------------------------------------------------------------------------
        @JvmStatic
        fun getRandomIndex(min: Int, max: Int): Int {
            return (Math.random() * (max - min + 1)).toInt() + min
        }

        @JvmStatic
        fun getResizedBitmap(bm: Bitmap?, newHeight: Int, newWidth: Int): Bitmap? {
            if (bm == null) return bm
            val width = bm.width
            val height = bm.height
            val scaleWidth = newWidth.toFloat() / width
            val scaleHeight = newHeight.toFloat() / height
            val matrix = Matrix()
            matrix.postScale(scaleWidth, scaleHeight)
            return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true)
        }

        @JvmStatic
        fun isConnectedToNetwork(context: Activity?): Boolean {
            val connectivityManager =
                    context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting ?: false
        }

        @JvmStatic
        fun checkAppIsInstalled(
                context: Context,
                packageName: String
        ): Boolean {
            if (!TextUtils.isEmpty(packageName)) {
                var pkgAppsList: List<ResolveInfo>
                pkgAppsList = getListAppInstalled(context)
                for (ri in pkgAppsList) {
                    if (packageName == ri.activityInfo.packageName) {
                        return true
                    }
                }
            }
            return false
        }

        @JvmStatic
        fun getListAppInstalled(context: Context): List<ResolveInfo> {
            val mainIntent = Intent(Intent.ACTION_MAIN, null)
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            return context.packageManager.queryIntentActivities(mainIntent, 0)
        }

        @JvmStatic
        fun drawableToBitmap(drawable: Drawable): Bitmap? {
            var bitmap: Bitmap? = null
            if (drawable is BitmapDrawable) {
                if (drawable.bitmap != null) {
                    return drawable.bitmap
                }
            }
            bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                Bitmap.createBitmap(
                        1,
                        1,
                        Bitmap.Config.ARGB_8888
                ) // Single color bitmap will be created of 1x1 pixel
            } else {
                Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        @JvmStatic
        fun deletePathFile(context: Context, fileName: String) {
            val cw = ContextWrapper(context)
            // path to /data/data/yourapp/app_data/imageDir
            val directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)
            // Create imageDir
            val file = File(directory, fileName)
            val isDelete = file.delete()
            MyLog.e("deletePathFile isDelete = $isDelete")
        }

        @JvmStatic
        fun getPathFile(context: Context, fileName: String): String? {
            val cw = ContextWrapper(context)
            // path to /data/data/yourapp/app_data/imageDir
            val directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)
            // Create imageDir
            val file = File(directory, fileName)

            if (file.exists())
                return file.absolutePath
            return ""
        }

        @JvmStatic
        fun saveToInternalStorage(
                context: Context,
                bitmapImage: Bitmap,
                fileName: String,
                override: Boolean
        ): String? {
            return saveToInternalStorage(context, bitmapImage, fileName, override, null)
        }

        @JvmStatic
        fun saveToInternalStorage(
                context: Context,
                bitmapImage: Bitmap,
                fileName: String,
                override: Boolean,
                compressFormat: Bitmap.CompressFormat?
        ): String? {
            val cw = ContextWrapper(context)
            // path to /data/data/yourapp/app_data/imageDir
            val directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)
            // Create imageDir
            val file = File(directory, fileName)

            if (file.exists()) {
                if (override) {
                    file.delete()
                } else return file.absolutePath
            }

            MyLog.d("saveToInternalStorage", "file = ${file.absoluteFile}")

            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                // Use the compress method on the BitMap object to write image to the OutputStream
                if (compressFormat != null)
                    bitmapImage.compress(compressFormat, 100, fos)
                else bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    fos!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return file.absolutePath
        }

        @JvmStatic
        fun changeFont(context: Context, textView: TextView, idFont: Int) {
            val typeface = ResourcesCompat.getFont(context, idFont)
            textView.typeface = typeface
        }

        @JvmStatic
        fun getCurrentLocale(): String? {
            return Locale.getDefault().language
        }

        //----------------------------------------------------------------------------------------------
        @JvmStatic
        fun md5(s: String): String? {
            try {
                // Create MD5 Hash
                val digest = MessageDigest.getInstance("MD5")
                digest.update(s.toByteArray())
                val messageDigest = digest.digest()

                // Create Hex String
                val hexString = StringBuffer()
                for (i in messageDigest.indices) hexString.append(Integer.toHexString(0xFF and messageDigest[i].toInt()))
                return hexString.toString()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            return ""
        }

        //----------------------------------------------------------------------------------------------
        @JvmStatic
        fun hideViewWithAnimationAlpha(
                view: View,
                time: Int,
                continueListener: OnContinueListener?
        ) {
            view.visibility = View.VISIBLE
            view.setOnClickListener { }
            view.alpha = 1f
            view.animate().apply {
                interpolator = LinearInterpolator()
                duration = 200
                alpha(0f)
                startDelay = ((time - 200).toLong())
                start()
            }
            Handler().postDelayed({
                continueListener?.onContinueListener()
                view.visibility = View.GONE
            }, time.toLong())
        }

        //----------------------------------------------------------------------------------------------
        @JvmStatic
        fun showViewWithAnimationAlpha(view: View) {
            view.visibility = View.VISIBLE
            view.alpha = 0f
            view.animate().apply {
                interpolator = LinearInterpolator()
                duration = 500
                alpha(1f)
                start()
            }
        }

        @JvmStatic
        fun convertDpToPixel(dp: Float, context: Context): Float {
            val resources = context.resources
            val metrics = resources.displayMetrics
            return dp * (metrics.densityDpi / 160f)
        }

        /**
         * This method converts device specific pixels to density independent pixels.
         *
         * @param px      A value in px (pixels) unit. Which we need to convert into db
         * @param context Context to get resources and device specific display metrics
         * @return A float value to represent dp equivalent to px value
         */
        @JvmStatic
        fun convertPixelsToDp(px: Int, context: Context): Float {
            val resources = context.resources
            val metrics = resources.displayMetrics
            return px / (metrics.densityDpi / 160f)
        }

        @JvmStatic
        fun pixelsToSp(context: Context, px: Float): Float {
            val scaledDensity = context.resources.displayMetrics.scaledDensity
            return px / scaledDensity
        }

        @JvmStatic
        fun spToPixels(context: Context, sp: Float): Float {
            val scaledDensity = context.resources.displayMetrics.scaledDensity
            return sp * scaledDensity
        }

        @JvmStatic
        fun getDisplayMetrics(mActivity: Activity): DisplayMetrics {
            val windowManager = mActivity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            display.getMetrics(displayMetrics)
            return displayMetrics;
        }
    }
}