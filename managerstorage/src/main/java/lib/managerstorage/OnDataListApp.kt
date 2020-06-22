package lib.managerstorage

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

interface OnDataListApp {
    fun OnSuccessListener(arrayList: ArrayList<JSONObject>)
    fun OnFailListener()
}