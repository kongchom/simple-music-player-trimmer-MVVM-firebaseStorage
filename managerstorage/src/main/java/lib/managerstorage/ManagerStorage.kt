package lib.managerstorage

import android.content.Context
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.InputStream


class ManagerStorage {
    companion object {
        val TAG: String = "ManagerStorage"
        const val BUCKET: String = "gs://scenic-casing-258204.appspot.com"
        lateinit var storage: FirebaseStorage

        //------------------------------------------------------------------------------------------
        @JvmStatic
        fun init() {
            storage = FirebaseStorage.getInstance(BUCKET)
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
                    onDownloadFile?.OnSuccessListener(fileSave)
                }.addOnFailureListener {
                    // Handle any errors
                    onDownloadFile?.OnFailListener()
                }
            }
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
                }

            }
            downloadFile(url, "filetmp", "", onDownloadFile)
        }
    }
}