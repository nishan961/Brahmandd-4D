package com.brahmandd.fourd

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import com.brahmandd.fourd.data.WallpaperCategory
import com.brahmandd.fourd.data.WallpaperImageStore
import com.brahmandd.fourd.data.RemoteWallpaper
import com.brahmandd.fourd.data.RemoteWallpaperCatalog
import com.brahmandd.fourd.data.WallpaperStateManager
import com.brahmandd.fourd.preview.BrahmanddPreviewActivity
import com.brahmandd.fourd.service.BrahmanddWallpaperService
import com.brahmandd.fourd.ui.dashboard.BrahmanddDashboard
import com.brahmandd.fourd.ui.theme.BrahmanddTheme
import java.io.IOException
import org.json.JSONException

class MainActivity : ComponentActivity() {

    private lateinit var wallpaperStateManager: WallpaperStateManager
    private var hasCustomShivaImage by mutableStateOf(false)
    private var remoteWallpapers by mutableStateOf<List<RemoteWallpaper>>(emptyList())
    private var remoteWallpaperImages by mutableStateOf<Map<String, Bitmap>>(emptyMap())
    private var selectedRemoteWallpaperId by mutableStateOf<String?>(null)
    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) saveShivaImage(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wallpaperStateManager = WallpaperStateManager(this)
        hasCustomShivaImage = wallpaperStateManager.hasCustomShivaImage
        selectedRemoteWallpaperId = wallpaperStateManager.selectedRemoteWallpaperId

        setContent {
            var selectedCategory by remember { mutableStateOf(wallpaperStateManager.activeCategory) }

            BrahmanddTheme {
                BrahmanddDashboard(
                    selectedCategory = selectedCategory,
                    remoteWallpapers = remoteWallpapers,
                    remoteWallpaperImages = remoteWallpaperImages,
                    selectedRemoteWallpaperId = selectedRemoteWallpaperId,
                    onCategorySelected = {
                        selectedCategory = it
                        selectedRemoteWallpaperId = null
                        wallpaperStateManager.selectedRemoteWallpaperId = null
                        wallpaperStateManager.activeCategory = it
                    },
                    onRemoteWallpaperSelected = {
                        selectedRemoteWallpaperId = it.id
                        wallpaperStateManager.selectedRemoteWallpaperId = it.id
                    },
                    onApplyWallpaper = ::applyLiveWallpaper,
                    hasCustomShivaImage = hasCustomShivaImage,
                    onSelectShivaImage = { imagePicker.launch("image/*") },
                    onPreview = {
                        startActivity(Intent(this, BrahmanddPreviewActivity::class.java))
                    },
                )
            }
        }
        loadRemoteWallpapers(getString(com.brahmandd.fourd.R.string.remote_wallpaper_catalog_url))
    }

    private fun loadRemoteWallpapers(catalogUrl: String) {
        if (catalogUrl.isBlank()) return

        Thread {
            try {
                val wallpapers = RemoteWallpaperCatalog.fetch(catalogUrl)
                val assets = wallpapers.mapNotNull { wallpaper ->
                    try {
                        wallpaper.id to RemoteWallpaperCatalog.cacheWallpaper(this, wallpaper)
                    } catch (error: IOException) {
                        Log.e(TAG, "Unable to cache remote wallpaper '${wallpaper.id}'.", error)
                        null
                    }
                }.toMap()

                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        remoteWallpapers = wallpapers.filter { it.id in assets }
                        remoteWallpaperImages = assets.mapValues { (_, wallpaperAssets) -> wallpaperAssets.image }
                        if (selectedRemoteWallpaperId !in remoteWallpapers.map { it.id }) {
                            selectedRemoteWallpaperId = null
                            wallpaperStateManager.selectedRemoteWallpaperId = null
                        }
                    }
                }
            } catch (error: IOException) {
                Log.e(TAG, "Unable to fetch the remote wallpaper catalog.", error)
                showCatalogError()
            } catch (error: JSONException) {
                Log.e(TAG, "The remote wallpaper catalog has an invalid format.", error)
                showCatalogError()
            } catch (error: SecurityException) {
                Log.e(TAG, "Unable to access the remote wallpaper catalog.", error)
                showCatalogError()
            }
        }.start()
    }

    private fun showCatalogError() {
        runOnUiThread {
            if (!isFinishing && !isDestroyed) {
                Toast.makeText(this, "Unable to load the remote wallpaper catalog.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveShivaImage(uri: Uri) {
        Thread {
            try {
                WallpaperImageStore.save(this, uri)
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        wallpaperStateManager.setHasCustomShivaImage(true)
                        hasCustomShivaImage = true
                        Toast.makeText(this, "Shiva photo added. Select Shiva • Kailash and apply the wallpaper.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (error: IOException) {
                Log.e(TAG, "Unable to save the selected Shiva image.", error)
                showImageError()
            } catch (error: SecurityException) {
                Log.e(TAG, "Unable to read the selected Shiva image.", error)
                showImageError()
            }
        }.start()
    }

    private fun showImageError() {
        runOnUiThread {
            if (!isFinishing && !isDestroyed) {
                Toast.makeText(this, "Unable to use that image. Please choose another.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun applyLiveWallpaper() {
        val component = ComponentName(this, BrahmanddWallpaperService::class.java)
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, component)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            startActivity(Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER))
        }
    }

    private companion object {
        const val TAG = "MainActivity"
    }
}
