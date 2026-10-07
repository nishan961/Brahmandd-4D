package com.brahmandd.fourd.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.security.MessageDigest
import kotlin.math.max

data class RemoteWallpaper(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val depthUrl: String,
)

data class RemoteWallpaperAssets(
    val image: Bitmap,
    val depth: Bitmap,
)

object RemoteWallpaperCatalog {
    private const val CONNECT_TIMEOUT_MILLIS = 10_000
    private const val READ_TIMEOUT_MILLIS = 10_000
    private const val MAX_IMAGE_DIMENSION = 512
    private const val MAX_DOWNLOAD_BYTES = 30L * 1024L * 1024L
    private const val CACHE_DIRECTORY = "remote_wallpapers"
    private const val TAG = "RemoteWallpaperCatalog"

    fun fetch(catalogUrl: String): List<RemoteWallpaper> {
        if (catalogUrl.isBlank()) return emptyList()

        val connection = openConnection(catalogUrl)
        try {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val wallpapers = when (val json = JSONTokener(response).nextValue()) {
                is JSONArray -> json
                is JSONObject -> json.getJSONArray("wallpapers")
                else -> throw JSONException("Expected a wallpaper array or an object with a wallpapers array.")
            }

            return buildList(wallpapers.length()) {
                for (index in 0 until wallpapers.length()) {
                    val item = wallpapers.getJSONObject(index)
                    add(
                        RemoteWallpaper(
                            id = item.getString("id").requiredValue("id"),
                            name = item.getString("name").requiredValue("name"),
                            description = item.optString("description"),
                            imageUrl = resolveUrl(
                                catalogUrl,
                                item.optString("image").ifBlank { item.optString("art_url") }
                                    .requiredValue("image"),
                            ),
                            depthUrl = resolveUrl(
                                catalogUrl,
                                item.optString("depth").ifBlank { item.optString("depth_url") }
                                    .requiredValue("depth"),
                            ),
                        )
                    )
                }
            }
        } finally {
            connection.disconnect()
        }
    }

    fun cacheWallpaper(context: Context, wallpaper: RemoteWallpaper): RemoteWallpaperAssets {
        val directory = wallpaperDirectory(context, wallpaper.id)
        if (!directory.exists() && !directory.mkdirs()) {
            throw IOException("Unable to create the remote wallpaper cache.")
        }

        val imageFile = File(directory, "image")
        val depthFile = File(directory, "depth")
        refreshCachedFile(wallpaper.imageUrl, imageFile)
        refreshCachedFile(wallpaper.depthUrl, depthFile)
        return RemoteWallpaperAssets(
            image = decodeCachedBitmap(imageFile),
            depth = decodeCachedBitmap(depthFile),
        )
    }

    fun loadCached(context: Context, wallpaperId: String): RemoteWallpaperAssets? {
        val directory = wallpaperDirectory(context, wallpaperId)
        val imageFile = File(directory, "image")
        val depthFile = File(directory, "depth")
        if (!imageFile.isFile || !depthFile.isFile) return null
        return RemoteWallpaperAssets(
            image = decodeCachedBitmap(imageFile),
            depth = decodeCachedBitmap(depthFile),
        )
    }

    private fun decodeCachedBitmap(file: File): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw IOException("The cached remote wallpaper image is not a supported image.")
        }

        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateSampleSize(bounds.outWidth, bounds.outHeight)
        }
        return BitmapFactory.decodeFile(file.absolutePath, options)
            ?: throw IOException("Unable to decode a cached remote wallpaper image.")
    }

    private fun refreshCachedFile(address: String, destination: File) {
        val temporaryFile = File(destination.parentFile, "${destination.name}.tmp")
        try {
            val connection = openConnection(address)
            try {
                connection.inputStream.use { input ->
                    FileOutputStream(temporaryFile).use { output ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var totalBytes = 0L
                        while (true) {
                            val bytesRead = input.read(buffer)
                            if (bytesRead < 0) break
                            totalBytes += bytesRead
                            if (totalBytes > MAX_DOWNLOAD_BYTES) {
                                throw IOException("The remote wallpaper file exceeds the download size limit.")
                            }
                            output.write(buffer, 0, bytesRead)
                        }
                        output.fd.sync()
                    }
                }
            } finally {
                connection.disconnect()
            }
            try {
                java.nio.file.Files.move(
                    temporaryFile.toPath(),
                    destination.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE,
                )
            } catch (error: java.nio.file.AtomicMoveNotSupportedException) {
                java.nio.file.Files.move(
                    temporaryFile.toPath(),
                    destination.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                )
            }
        } catch (error: IOException) {
            if (!destination.isFile) throw error
            Log.w(TAG, "Using cached remote wallpaper file after a download failure.", error)
        } finally {
            if (temporaryFile.exists()) temporaryFile.delete()
        }
    }

    private fun wallpaperDirectory(context: Context, id: String): File {
        val key = MessageDigest.getInstance("SHA-256")
            .digest(id.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
        return File(File(context.filesDir, CACHE_DIRECTORY), key)
    }

    private fun resolveUrl(catalogUrl: String, value: String): String {
        val resolved = try {
            URI(catalogUrl).resolve(value).toURL().toString()
        } catch (error: URISyntaxException) {
            throw JSONException("Invalid remote wallpaper asset URL.", error)
        } catch (error: MalformedURLException) {
            throw JSONException("Invalid remote wallpaper asset URL.", error)
        } catch (error: IllegalArgumentException) {
            throw JSONException("Invalid remote wallpaper asset URL.", error)
        }
        if (!resolved.startsWith("https://") && !resolved.startsWith("http://")) {
            throw JSONException("Remote wallpaper asset URLs must use HTTP or HTTPS.")
        }
        return resolved
    }

    private fun openConnection(address: String): HttpURLConnection {
        val url = try {
            URL(address)
        } catch (error: IllegalArgumentException) {
            throw IOException("Invalid remote wallpaper URL.", error)
        }
        if (url.protocol != "https" && url.protocol != "http") {
            throw IOException("Remote wallpaper URLs must use HTTP or HTTPS.")
        }

        return (url.openConnection() as? HttpURLConnection)?.apply {
            connectTimeout = CONNECT_TIMEOUT_MILLIS
            readTimeout = READ_TIMEOUT_MILLIS
            instanceFollowRedirects = true
        } ?: throw IOException("Unable to open the remote wallpaper URL.")
    }

    private fun calculateSampleSize(width: Int, height: Int): Int {
        var sampleSize = 1
        while (max(width / sampleSize, height / sampleSize) > MAX_IMAGE_DIMENSION) {
            sampleSize *= 2
        }
        return sampleSize
    }

    private fun String.requiredValue(field: String): String {
        if (isBlank()) throw JSONException("Wallpaper field '$field' must not be blank.")
        return this
    }
}
