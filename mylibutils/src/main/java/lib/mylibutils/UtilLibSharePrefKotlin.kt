@file:Suppress("DEPRECATION")

package lib.mylibutils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object UtilLibSharePrefKotlin {
    private var mSharePref: SharedPreferences? = null
    @JvmStatic
    fun init(context: Context?) {
        if (mSharePref == null) mSharePref =
                PreferenceManager.getDefaultSharedPreferences(context)
    }
    @JvmStatic
    fun clearAll() {
        mSharePref!!.edit().clear().apply()
        mSharePref!!.edit().apply()
    }
    @JvmStatic
    fun removeValueWithKey(key: String?) {
        mSharePref!!.edit().remove(key).apply()
    }
    @JvmStatic
    fun putString(key: String?, value: String?) {
        val editor = mSharePref!!.edit()
        editor.putString(key, value)
        editor.apply()
    }
    @JvmStatic
    fun getString(key: String?, defaultValues: String?): String? {
        return mSharePref!!.getString(key, defaultValues)
    }
    @JvmStatic
    fun putInt(key: String?, value: Int) {
        val editor = mSharePref!!.edit()
        editor.putInt(key, value)
        editor.apply()
    }
    @JvmStatic
    fun getInt(key: String?, defaultValues: Int): Int {
        return mSharePref!!.getInt(key, defaultValues)
    }
    @JvmStatic
    fun putLong(key: String?, value: Long) {
        val editor = mSharePref!!.edit()
        editor.putLong(key, value)
        editor.apply()
    }
    @JvmStatic
    fun getLong(key: String?, defaultValues: Long): Long {
        return mSharePref!!.getLong(key, defaultValues)
    }
    @JvmStatic
    fun putBoolean(key: String?, value: Boolean) {
        val editor = mSharePref!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }
    @JvmStatic
    fun getBoolean(key: String?, defaultValues: Boolean): Boolean {
        return mSharePref!!.getBoolean(key, defaultValues)
    }
    @JvmStatic
    fun contains(keySaveDataRemote: String): Boolean {
        return mSharePref!!.contains(keySaveDataRemote)
    }
}