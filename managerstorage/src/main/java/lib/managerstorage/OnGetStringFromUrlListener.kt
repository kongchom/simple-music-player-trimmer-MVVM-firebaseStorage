package lib.managerstorage

import java.io.File

interface OnGetStringFromUrlListener {
    fun OnSuccessListener(str: String)
    fun OnFailListener()
}