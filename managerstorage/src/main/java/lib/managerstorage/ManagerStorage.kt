package lib.managerstorage

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.google.firebase.storage.FirebaseStorage
import lib.mylibutils.MyLog
import lib.mylibutils.UtilLibKotlin
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream


class ManagerStorage {
    companion object {
        val TAG: String = "ManagerStorage"
        const val BUCKET: String = ""
        lateinit var storage: FirebaseStorage

        //------------------------------------------------------------------------------------------
        @JvmStatic
        fun init() {
            storage = FirebaseStorage.getInstance(BUCKET)
        }

        @JvmStatic
        fun getListALL(path: String, onListAllListener: OnListAllListener) {
            MyLog.e(TAG, "getListALL Start path = $path")
            val listRef = storage.reference.child(path)
            listRef.listAll()
                    .addOnSuccessListener { listResult ->
                        //                        listResult.prefixes.forEach { prefix ->
//                            MyLog.d(TAG, "prefix = " + prefix.path)
//                        }
//


                        onListAllListener.OnSuccessListener(listResult.items.size)
                    }
                    .addOnFailureListener {
                        // Uh-oh, an error occurred!
                        MyLog.e(TAG, "getListALL addOnFailureListener path = $path")
                        onListAllListener.OnFailListener()
                    }

        }

        @JvmStatic
        fun getList(path: String, onListAllListener: OnListListener) {
            MyLog.e(TAG, "getListALL Start path = $path")
            val listRef = storage.reference.child(path)
            listRef.listAll()
                    .addOnSuccessListener { listResult ->
                        var listName = ArrayList<String>()
                        MyLog.e(TAG, "listResult.items.size = " + listResult.items.size)
                        listResult.prefixes.forEach { item ->
                            listName.add(item.name)
                            MyLog.e(TAG, "item.name = " + item.name)
                        }
                        onListAllListener.OnSuccessListener(listName)
                    }
                    .addOnFailureListener {
                        // Uh-oh, an error occurred!
                        MyLog.e(TAG, "getListALL addOnFailureListener path = $path")
                        onListAllListener.OnFailListener()
                    }

        }

        //------------------------------------------------------------------------------------------
        @JvmStatic
        fun downloadFile(
                path: String,
                saveName: String,
                saveType: String,
                onDownloadFile: OnDownloadFileListener?
        ) {
            val storageReferenceChild = storage.reference.child(path)
            val localFile = File.createTempFile(saveName, saveType)

            storageReferenceChild.getFile(localFile).addOnSuccessListener {
                // Local temp file has been created
                Log.e(
                        TAG,
                        "downloadFile success localFile.absolutePath = " + localFile.absolutePath
                )
                onDownloadFile?.OnSuccessListener(localFile)

            }.addOnFailureListener {
                // Handle any errors
                Log.e(TAG, "downloadFile fail")
                onDownloadFile?.OnFailListener()
            }
        }

        //------------------------------------------------------------------------------------------
        @JvmStatic
        fun downloadFileToExternalStorage(
                path: String,
                folderName: String,
                saveName: String,
                onDownloadFile: OnDownloadFileListener?
        ) {
            val storageReferenceChild = storage.reference.child(path)
            val fileSave = File(folderName, saveName)

            if (fileSave.exists()) {
                onDownloadFile?.OnSuccessListener(fileSave)
            } else {
                storageReferenceChild.getFile(fileSave).addOnSuccessListener {
                    // Local temp file has been created
                    MyLog.e(TAG, "downloadFile success localFile.absolutePath = " + fileSave.absolutePath)
                    onDownloadFile?.OnSuccessListener(fileSave)
                }.addOnFailureListener {
                    // Handle any errors
                    MyLog.e(TAG, "downloadFile fail")
                    onDownloadFile?.OnFailListener()
                }
            }
        }

        //------------------------------------------------------------------------------------------
        @JvmStatic
        fun loadThumbToImageView(mAct: Activity,
                                 urlImage: String,
                                 imgItem: ImageView,
                                 imgLoading: ImageView?,
                                 imgWidth: Int,
                                 imgHeight: Int) {

            val storageReferenceChild = storage.reference.child(urlImage)
            MyLog.e(TAG, "path = $urlImage")

            GlideApp.with(mAct)
                    .load(storageReferenceChild)
                    .override(imgWidth, imgHeight)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            if (imgLoading != null) imgLoading.visibility = View.GONE
                            return false
                        }
                    })
                    .into(imgItem)
        }

        //------------------------------------------------------------------------------------------
        const val KEY_LIST_APP: String = "list_app"

        @JvmStatic
        fun getDataListApp(context: Context, onDataListApp: OnDataListApp?) {
            val onDownloadFile: OnDownloadFileListener
            onDownloadFile = object : OnDownloadFileListener {
                override fun OnSuccessListener(file: File) {
                    val inputStream: InputStream = file.inputStream()
                    val inputString = inputStream.bufferedReader().use { it.readText() }
                    val jsonObject: JSONObject = JSONObject(inputString)
                    val jsonArray: JSONArray = jsonObject.getJSONArray(KEY_LIST_APP)
                    val arrayListResult = removePackageNameIsInstall(jsonArray, context)
                    onDataListApp?.OnSuccessListener(arrayListResult)
                    Log.e(TAG, "jsonObject = $jsonObject")
                }

                override fun OnFailListener() {
                    onDataListApp?.OnFailListener()
                    if (MyLog.isDEBUG()) {
                        Toast.makeText(context, "getDataListApp fail", Toast.LENGTH_LONG).show();
                    }
                }

            }
            downloadFile("API/list_more_app", "listapp", "", onDownloadFile)
        }

        //------------------------------------------------------------------------------------------
        @JvmStatic
        fun removePackageNameIsInstall(
                jsonArray: JSONArray,
                context: Context
        ): ArrayList<JSONObject> {
            var arrayListResult: ArrayList<JSONObject> = ArrayList<JSONObject>()

            for (index in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(index)
                val package_name: String = item.getString("package_name")
                if (!UtilLibKotlin.checkAppIsInstalled(context, package_name)) {
                    arrayListResult.add(item)
                }
            }

            return arrayListResult
        }

        //------------------------------------------------------------------------------------------
        @JvmStatic
        fun getStringFromUrl(context: Context, url: String, onGetStringFromUrlListener: OnGetStringFromUrlListener?) {
            val onDownloadFile: OnDownloadFileListener
            onDownloadFile = object : OnDownloadFileListener {
                override fun OnSuccessListener(file: File) {
                    val inputStream: InputStream = file.inputStream()
                    val inputString = inputStream.bufferedReader().use { it.readText() }
                    onGetStringFromUrlListener?.OnSuccessListener(inputString)
                    Log.e(TAG, "inputString = $inputString")

                    inputStream.close()
                    file.delete()
                }

                override fun OnFailListener() {
                    onGetStringFromUrlListener?.OnFailListener()

                    if (MyLog.isDEBUG()) {
                        Toast.makeText(context, "getStringFromUrl fail", Toast.LENGTH_LONG).show();
                    }
                }

            }
            downloadFile(url, "filetmp", "", onDownloadFile)
        }
    }
}