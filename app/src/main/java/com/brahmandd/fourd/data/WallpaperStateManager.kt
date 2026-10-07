package com.brahmandd.fourd.data

import android.content.Context
import android.content.SharedPreferences

class WallpaperStateManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var activeCategory: WallpaperCategory
        get() = WallpaperCategory.fromKey(prefs.getString(KEY_ACTIVE_CATEGORY, WallpaperCategory.DEEP_SPACE.key))
        set(value) {
            prefs.edit().putString(KEY_ACTIVE_CATEGORY, value.key).apply()
        }

    var selectedRemoteWallpaperId: String?
        get() = prefs.getString(KEY_SELECTED_REMOTE_WALLPAPER, null)
        set(value) {
            prefs.edit().putString(KEY_SELECTED_REMOTE_WALLPAPER, value).apply()
        }

    val hasCustomShivaImage: Boolean
        get() = prefs.getBoolean(KEY_HAS_CUSTOM_SHIVA_IMAGE, false)

    fun setHasCustomShivaImage(value: Boolean) {
        prefs.edit().putBoolean(KEY_HAS_CUSTOM_SHIVA_IMAGE, value).apply()
    }

    companion object {
        private const val PREFS_NAME = "brahmandd_wallpaper_state"
        private const val KEY_ACTIVE_CATEGORY = "active_category"
        private const val KEY_SELECTED_REMOTE_WALLPAPER = "selected_remote_wallpaper"
        private const val KEY_HAS_CUSTOM_SHIVA_IMAGE = "has_custom_shiva_image"
    }
}
