package com.brahmandd.fourd.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.max

object WallpaperImageStore {
    private const val FILE_NAME = "custom_shiva_wallpaper.jpg"
    private const val MAX_IMAGE_DIMENSION = 2048

    fun save(context: Context, uri: Uri) {
        val resolver = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            ?: throw IOException("Unable to open the selected image.")
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw IOException("The selected file is not a supported image.")
        }

        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateSampleSize(bounds.outWidth, bounds.outHeight)
        }
        val bitmap = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: throw IOException("Unable to decode the selected image.")

        val destination = File(context.filesDir, FILE_NAME)
        val temporaryFile = File(context.filesDir, "$FILE_NAME.tmp")
        try {
            FileOutputStream(temporaryFile).use { output ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)) {
                    throw IOException("Unable to save the selected image.")
                }
                output.fd.sync()
            }
            if (!temporaryFile.renameTo(destination)) {
                throw IOException("Unable to save the selected image.")
            }
        } finally {
            bitmap.recycle()
            if (temporaryFile.exists()) temporaryFile.delete()
        }
    }

    fun load(context: Context): Bitmap? {
        val imageFile = File(context.filesDir, FILE_NAME)
        if (!imageFile.isFile) return null

        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        if (bitmap == null) {
            throw IOException("The saved Shiva image could not be decoded.")
        }
        return bitmap
    }

    private fun calculateSampleSize(width: Int, height: Int): Int {
        var sampleSize = 1
        while (max(width / sampleSize, height / sampleSize) > MAX_IMAGE_DIMENSION) {
            sampleSize *= 2
        }
        return sampleSize
    }
}
