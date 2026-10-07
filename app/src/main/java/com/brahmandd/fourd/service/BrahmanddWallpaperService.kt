package com.brahmandd.fourd.service

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.brahmandd.fourd.data.WallpaperImageStore
import com.brahmandd.fourd.data.RemoteWallpaperAssets
import com.brahmandd.fourd.data.RemoteWallpaperCatalog
import com.brahmandd.fourd.data.WallpaperStateManager
import com.brahmandd.fourd.rendering.BrahmanddRenderer
import com.brahmandd.fourd.rendering.BrahmanddRenderer.RemoteWallpaperParallaxRenderer
import java.io.IOException

class BrahmanddWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = BrahmanddEngine()

    inner class BrahmanddEngine : Engine(), SensorEventListener {

        private val sensorManager = applicationContext.getSystemService(SensorManager::class.java) as SensorManager
        private val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        private val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        private val stateManager = WallpaperStateManager(applicationContext)
        private val remoteWallpaperAssets: RemoteWallpaperAssets? =
            stateManager.selectedRemoteWallpaperId?.let { wallpaperId ->
                try {
                    RemoteWallpaperCatalog.loadCached(applicationContext, wallpaperId)
                } catch (error: IOException) {
                    Log.e(TAG, "Unable to load the selected remote wallpaper.", error)
                    null
                }
            }
        private val customShivaImage: Bitmap? = if (stateManager.hasCustomShivaImage) {
            try {
                WallpaperImageStore.load(applicationContext)
            } catch (error: IOException) {
                Log.e(TAG, "Unable to load the saved Shiva image.", error)
                null
            }
        } else {
            null
        }
        private val renderHandler = Handler(Looper.getMainLooper())
        private var renderRunnable: Runnable? = null
        private var isVisible = false
        private var targetX = 0f
        private var targetY = 0f
        private var currentX = 0f
        private var currentY = 0f
        private val rotationMatrix = FloatArray(9)
        private val orientation = FloatArray(3)
        private val remoteWallpaperRenderer = RemoteWallpaperParallaxRenderer()

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            surfaceHolder.setFormat(PixelFormat.RGBA_8888)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            isVisible = visible
            if (visible) {
                registerSensors()
                startRenderLoop()
            } else {
                unregisterSensors()
                stopRenderLoop()
            }
        }

        override fun onDestroy() {
            unregisterSensors()
            stopRenderLoop()
            super.onDestroy()
        }

        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_ROTATION_VECTOR -> {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    targetX = orientation[1] * 1.8f
                    targetY = orientation[2] * 1.8f
                }
                Sensor.TYPE_ACCELEROMETER -> {
                    val x = event.values[0] / SensorManager.GRAVITY_EARTH
                    val y = event.values[1] / SensorManager.GRAVITY_EARTH
                    targetX = x * 1.4f
                    targetY = y * 1.4f
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

        private fun registerSensors() {
            rotationVectorSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
            accelerometerSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
        }

        private fun unregisterSensors() {
            sensorManager.unregisterListener(this)
        }

        private fun startRenderLoop() {
            if (renderRunnable != null) return

            renderRunnable = object : Runnable {
                override fun run() {
                    if (!isVisible) return

                    val holder = surfaceHolder ?: return
                    val canvas: Canvas? = holder.lockCanvas()
                    if (canvas == null) {
                        renderHandler.postDelayed(this, 1000L / 60)
                        return
                    }

                    try {
                        currentX += (targetX - currentX) * 0.12f
                        currentY += (targetY - currentY) * 0.12f
                        BrahmanddRenderer.render(
                            canvas = canvas,
                            width = canvas.width,
                            height = canvas.height,
                            category = stateManager.activeCategory,
                            tiltX = currentX,
                            tiltY = currentY,
                            customShivaImage = customShivaImage,
                            remoteWallpaperAssets = remoteWallpaperAssets,
                            remoteWallpaperRenderer = remoteWallpaperRenderer,
                        )
                    } finally {
                        holder.unlockCanvasAndPost(canvas)
                    }

                    renderHandler.postDelayed(this, 1000L / 60)
                }
            }

            renderHandler.post(renderRunnable!!)
        }

        private fun stopRenderLoop() {
            renderRunnable?.let { renderHandler.removeCallbacks(it) }
            renderRunnable = null
        }
    }

    private companion object {
        const val TAG = "BrahmanddWallpaper"
    }
}
