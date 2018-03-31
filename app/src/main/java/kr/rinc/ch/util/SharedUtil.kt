package kr.rinc.ch.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


object SharedUtil {
  private var KEY = "KEYKEY"

  fun getList(ctx: Context): String = getSharedPreferences(ctx).getString(KEY, "")

  fun setList(ctx: Context, imageList: String) {
    val editor = getSharedPreferences(ctx).edit()
    editor.putString(KEY, imageList)
    editor.apply()
  }

  private fun getSharedPreferences(ctx: Context): SharedPreferences {

    return PreferenceManager.getDefaultSharedPreferences(ctx)
  }
}