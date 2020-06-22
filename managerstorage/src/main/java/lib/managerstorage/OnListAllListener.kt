package lib.managerstorage

import java.io.File

interface OnListAllListener {
    fun OnSuccessListener(totalItem: Int)
    fun OnFailListener()
}