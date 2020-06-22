package lib.managerstorage

import java.io.File

interface OnDownloadFileListener {
    fun OnSuccessListener(file: File)
    fun OnFailListener()
}