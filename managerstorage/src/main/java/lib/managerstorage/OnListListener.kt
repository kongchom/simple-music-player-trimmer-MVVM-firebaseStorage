package lib.managerstorage

interface OnListListener {
    fun OnSuccessListener(list: ArrayList<String>)
    fun OnFailListener()
}