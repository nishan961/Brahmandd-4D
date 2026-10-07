package com.brahmandd.fourd.preview

import android.content.Context
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.util.AttributeSet
import android.view.View
import com.brahmandd.fourd.data.WallpaperImageStore
import com.brahmandd.fourd.data.RemoteWallpaperAssets
import com.brahmandd.fourd.data.RemoteWallpaperCatalog
import com.brahmandd.fourd.data.WallpaperStateManager
import com.brahmandd.fourd.rendering.BrahmanddRenderer
import com.brahmandd.fourd.rendering.BrahmanddRenderer.RemoteWallpaperParallaxRenderer
import java.io.IOException

class BrahmanddPreviewView @JvmOverloads constructor(
    context: Context,
    private val wallpaperStateManager: WallpaperStateManager,
    attrs: AttributeSet? = null,
) : View(context, attrs), SensorEventListener {

    private val sensorManager = context.getSystemService(SensorManager::class.java)
    private val rotationSensor = (sensorManager as? SensorManager)?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val customShivaImage: Bitmap? = if (wallpaperStateManager.hasCustomShivaImage) {
        try {
            WallpaperImageStore.load(context)
        } catch (error: IOException) {
            Log.e(TAG, "Unable to load the saved Shiva image for preview.", error)
            null
        }
    } else {
        null
    }
    private val remoteWallpaperAssets: RemoteWallpaperAssets? =
        wallpaperStateManager.selectedRemoteWallpaperId?.let { wallpaperId ->
            try {
                RemoteWallpaperCatalog.loadCached(context, wallpaperId)
            } catch (error: IOException) {
                Log.e(TAG, "Unable to load the selected remote wallpaper for preview.", error)
                null
            }
        }
    private var targetX = 0f
    private var targetY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)
    private val remoteWallpaperRenderer = RemoteWallpaperParallaxRenderer()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        rotationSensor?.let {
            (sensorManager as SensorManager).registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME,
            )
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        (sensorManager as? SensorManager)?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientation)
            targetX = orientation[1] * 1.8f
            targetY = orientation[2] * 1.8f
            postInvalidateOnAnimation()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onDraw(canvas: android.graphics.Canvas) {
        super.onDraw(canvas)
        currentX += (targetX - currentX) * 0.12f
        currentY += (targetY - currentY) * 0.12f
        BrahmanddRenderer.render(
            canvas = canvas,
            width = width,
            height = height,
            category = wallpaperStateManager.activeCategory,
            tiltX = currentX,
            tiltY = currentY,
            customShivaImage = customShivaImage,
            remoteWallpaperAssets = remoteWallpaperAssets,
            remoteWallpaperRenderer = remoteWallpaperRenderer,
        )
        postInvalidateOnAnimation()
    }

    private companion object {
        const val TAG = "BrahmanddPreview"
    }
}
