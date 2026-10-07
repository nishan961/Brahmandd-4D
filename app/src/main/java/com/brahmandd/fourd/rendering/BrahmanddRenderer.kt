package com.brahmandd.fourd.rendering

import android.graphics.BlurMaskFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import com.brahmandd.fourd.data.RemoteWallpaperAssets
import com.brahmandd.fourd.data.WallpaperCategory
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sin

object BrahmanddRenderer {
    fun render(
        canvas: Canvas,
        width: Int,
        height: Int,
        category: WallpaperCategory,
        tiltX: Float,
        tiltY: Float,
        customShivaImage: Bitmap? = null,
        remoteWallpaperAssets: RemoteWallpaperAssets? = null,
        remoteWallpaperRenderer: RemoteWallpaperParallaxRenderer? = null,
    ) {
        if (remoteWallpaperAssets != null) {
            checkNotNull(remoteWallpaperRenderer) {
                "A remote wallpaper renderer is required when rendering remote wallpaper assets."
            }.render(canvas, width, height, tiltX, tiltY, remoteWallpaperAssets)
            return
        }

        when (category) {
            WallpaperCategory.DEEP_SPACE -> renderDeepSpace(canvas, width, height, tiltX, tiltY)
            WallpaperCategory.CYBER_GRID -> renderCyberGrid(canvas, width, height, tiltX, tiltY)
            WallpaperCategory.AMOLED_QUANTUM -> renderAmoledQuantum(canvas, width, height, tiltX, tiltY)
            WallpaperCategory.SHIVA -> renderShiva(canvas, width, height, tiltX, tiltY, customShivaImage)
        }
    }

    private fun renderShiva(
        canvas: Canvas,
        width: Int,
        height: Int,
        tiltX: Float,
        tiltY: Float,
        customImage: Bitmap?,
    ) {
        canvas.save()
        canvas.scale(width / 1000f, height / 1800f)
        if (customImage != null) {
            val scale = maxOf(1000f / customImage.width, 1800f / customImage.height) * 1.08f
            val imageWidth = customImage.width * scale
            val imageHeight = customImage.height * scale
            val offsetX = tiltX * 24f
            val offsetY = tiltY * 24f
            val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            canvas.drawBitmap(
                customImage,
                null,
                RectF(
                    (1000f - imageWidth) / 2f + offsetX,
                    (1800f - imageHeight) / 2f + offsetY,
                    (1000f + imageWidth) / 2f + offsetX,
                    (1800f + imageHeight) / 2f + offsetY,
                ),
                imagePaint,
            )
            canvas.restore()
            return
        }

        canvas.drawRect(
            0f,
            0f,
            1000f,
            1800f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    0f,
                    0f,
                    0f,
                    1800f,
                    intArrayOf(Color.rgb(3, 8, 28), Color.rgb(12, 26, 62), Color.rgb(7, 13, 30)),
                    null,
                    Shader.TileMode.CLAMP,
                )
            },
        )

        val auraPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                500f + tiltX * 20f,
                820f + tiltY * 22f,
                620f,
                intArrayOf(Color.argb(90, 56, 189, 248), Color.argb(35, 129, 140, 248), Color.TRANSPARENT),
                null,
                Shader.TileMode.CLAMP,
            )
        }
        canvas.drawCircle(500f + tiltX * 20f, 820f + tiltY * 22f, 620f, auraPaint)

        for (i in 0 until 100) {
            val seed = i * 31.7f
            val x = (sin(seed * 1.9f) * 520f + 500f + tiltX * (i % 4 + 1) * 5f)
            val y = (cos(seed * 1.3f) * 760f + 760f + tiltY * (i % 5 + 1) * 5f)
            val star = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(90 + i % 130, 205, 230, 255)
            }
            canvas.drawCircle(x, y, 1.5f + i % 3, star)
        }

        canvas.save()
        canvas.translate(tiltX * 14f, tiltY * 10f)
        val distantMountain = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(29, 61, 103)
        }
        val mountainPath = Path().apply {
            moveTo(-100f, 1120f)
            lineTo(150f, 760f)
            lineTo(285f, 970f)
            lineTo(500f, 590f)
            lineTo(745f, 965f)
            lineTo(875f, 785f)
            lineTo(1100f, 1120f)
            close()
        }
        canvas.drawPath(mountainPath, distantMountain)
        val snow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(150, 180, 220, 255)
        }
        canvas.drawPath(
            Path().apply {
                moveTo(500f, 590f)
                lineTo(430f, 708f)
                lineTo(494f, 682f)
                lineTo(536f, 730f)
                lineTo(555f, 682f)
                close()
            },
            snow,
        )
        canvas.restore()

        canvas.save()
        canvas.translate(tiltX * 27f, tiltY * 20f)
        val foregroundMountain = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(11, 30, 57)
        }
        canvas.drawPath(
            Path().apply {
                moveTo(-100f, 1380f)
                lineTo(180f, 1040f)
                lineTo(355f, 1250f)
                lineTo(535f, 1000f)
                lineTo(760f, 1270f)
                lineTo(930f, 1070f)
                lineTo(1100f, 1400f)
                lineTo(1100f, 1800f)
                lineTo(-100f, 1800f)
                close()
            },
            foregroundMountain,
        )
        canvas.restore()

        canvas.save()
        canvas.translate(tiltX * 43f, tiltY * 34f)
        val figureGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                500f,
                1010f,
                430f,
                intArrayOf(Color.argb(75, 34, 211, 238), Color.argb(25, 59, 130, 246), Color.TRANSPARENT),
                null,
                Shader.TileMode.CLAMP,
            )
        }
        canvas.drawCircle(500f, 1010f, 430f, figureGlow)

        val tridentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(211, 174, 112)
            strokeWidth = 9f
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }
        canvas.drawLine(805f, 660f, 805f, 1390f, tridentPaint)
        canvas.drawPath(
            Path().apply {
                moveTo(755f, 760f)
                cubicTo(755f, 670f, 790f, 645f, 805f, 645f)
                cubicTo(820f, 645f, 855f, 670f, 855f, 760f)
                moveTo(805f, 645f)
                lineTo(805f, 585f)
                moveTo(755f, 760f)
                lineTo(730f, 735f)
                moveTo(855f, 760f)
                lineTo(880f, 735f)
            },
            tridentPaint,
        )
        canvas.drawCircle(805f, 650f, 13f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(211, 174, 112)
            style = Paint.Style.FILL
        })

        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                300f,
                760f,
                700f,
                1390f,
                intArrayOf(Color.rgb(81, 174, 217), Color.rgb(28, 91, 145), Color.rgb(14, 44, 83)),
                null,
                Shader.TileMode.CLAMP,
            )
        }
        val body = Path().apply {
            moveTo(425f, 760f)
            cubicTo(370f, 785f, 338f, 845f, 325f, 920f)
            lineTo(277f, 1040f)
            cubicTo(295f, 1070f, 330f, 1080f, 351f, 1045f)
            lineTo(412f, 930f)
            lineTo(405f, 1120f)
            cubicTo(340f, 1170f, 290f, 1240f, 270f, 1325f)
            cubicTo(330f, 1375f, 425f, 1390f, 500f, 1390f)
            cubicTo(575f, 1390f, 670f, 1375f, 730f, 1325f)
            cubicTo(710f, 1240f, 660f, 1170f, 595f, 1120f)
            lineTo(588f, 930f)
            lineTo(649f, 1045f)
            cubicTo(670f, 1080f, 705f, 1070f, 723f, 1040f)
            lineTo(675f, 920f)
            cubicTo(662f, 845f, 630f, 785f, 575f, 760f)
            close()
        }
        canvas.drawPath(body, bodyPaint)

        val legs = Path().apply {
            moveTo(408f, 1185f)
            cubicTo(345f, 1190f, 270f, 1240f, 242f, 1310f)
            cubicTo(225f, 1350f, 270f, 1380f, 330f, 1380f)
            lineTo(452f, 1375f)
            cubicTo(470f, 1410f, 530f, 1410f, 548f, 1375f)
            lineTo(670f, 1380f)
            cubicTo(730f, 1380f, 775f, 1350f, 758f, 1310f)
            cubicTo(730f, 1240f, 655f, 1190f, 592f, 1185f)
            cubicTo(560f, 1225f, 440f, 1225f, 408f, 1185f)
            close()
        }
        canvas.drawPath(legs, bodyPaint)
        canvas.drawPath(
            Path().apply {
                moveTo(500f, 1188f)
                cubicTo(454f, 1240f, 373f, 1280f, 300f, 1330f)
                cubicTo(380f, 1350f, 440f, 1330f, 500f, 1287f)
                cubicTo(560f, 1330f, 620f, 1350f, 700f, 1330f)
                cubicTo(627f, 1280f, 546f, 1240f, 500f, 1188f)
                close()
            },
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(17, 57, 99) },
        )

        canvas.drawCircle(500f, 675f, 104f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(410f, 590f, 590f, 770f, Color.rgb(125, 205, 231), Color.rgb(44, 116, 169), Shader.TileMode.CLAMP)
        })
        val hairPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(10, 22, 41) }
        canvas.drawOval(395f, 545f, 605f, 680f, hairPaint)
        canvas.drawCircle(500f, 540f, 55f, hairPaint)
        canvas.drawPath(
            Path().apply {
                moveTo(417f, 621f)
                cubicTo(435f, 570f, 462f, 592f, 473f, 540f)
                cubicTo(495f, 590f, 526f, 580f, 545f, 535f)
                cubicTo(548f, 594f, 575f, 579f, 588f, 626f)
                close()
            },
            hairPaint,
        )
        val moonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(240, 221, 170)
            style = Paint.Style.STROKE
            strokeWidth = 10f
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawArc(513f, 490f, 580f, 560f, 205f, 250f, false, moonPaint)

        val facePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(12, 43, 77)
            strokeWidth = 5f
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }
        canvas.drawLine(455f, 670f, 480f, 675f, facePaint)
        canvas.drawLine(520f, 675f, 545f, 670f, facePaint)
        canvas.drawLine(500f, 674f, 495f, 710f, facePaint)
        canvas.drawLine(488f, 726f, 512f, 726f, facePaint)
        val tilakPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(239, 221, 177)
            strokeWidth = 5f
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawLine(472f, 630f, 528f, 630f, tilakPaint)
        canvas.drawLine(478f, 644f, 522f, 644f, tilakPaint)
        canvas.drawCircle(500f, 654f, 5f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(248, 124, 90) })

        val necklacePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(224, 177, 110)
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        canvas.drawArc(433f, 740f, 567f, 835f, 10f, 160f, false, necklacePaint)
        for (i in 0 until 9) {
            val angle = Math.PI * (0.12 + i * 0.095)
            canvas.drawCircle(
                500f + cos(angle).toFloat() * 62f,
                785f + sin(angle).toFloat() * 32f,
                4f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(155, 91, 58) },
            )
        }
        canvas.restore()
        canvas.restore()
    }

    private fun renderDeepSpace(
        canvas: Canvas,
        width: Int,
        height: Int,
        tiltX: Float,
        tiltY: Float,
    ) {
        val background = LinearGradient(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            intArrayOf(
                Color.parseColor("#030712"),
                Color.parseColor("#0F172A"),
                Color.parseColor("#111827"),
            ),
            floatArrayOf(0f, 0.55f, 1f),
            Shader.TileMode.CLAMP,
        )
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = background
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        val nebulaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                width * 0.48f,
                height * 0.42f,
                min(width, height) * 0.7f,
                intArrayOf(
                    Color.argb(80, 56, 189, 248),
                    Color.argb(60, 129, 140, 248),
                    Color.argb(0, 0, 0, 0),
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP,
            )
        }
        canvas.drawCircle(width * 0.5f, height * 0.42f, min(width, height) * 0.52f, nebulaPaint)

        for (i in 0 until 220) {
            val seed = i * 17.13f
            val x = (sin(seed * 2.3f) * width * 0.75f + width * 0.5f + tiltX * 120f * (i % 5 + 1) * 0.3f)
            val y = (cos(seed * 1.7f) * height * 0.7f + height * 0.5f + tiltY * 110f * (i % 7 + 1) * 0.28f)
            val radius = 1.2f + ((i % 9) * 0.8f)
            val alpha: Int = 60 + (i % 180)
            val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(alpha, 255, 255, 255)
            }
            canvas.drawCircle(x, y, radius, starPaint)
        }

        val offsetX = width * 0.5f + tiltX * width * 0.18f
        val offsetY = height * 0.5f + tiltY * height * 0.18f
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                offsetX,
                offsetY,
                min(width, height) * 0.22f,
                intArrayOf(
                    Color.argb(255, 56, 189, 248),
                    Color.argb(200, 129, 140, 248),
                    Color.argb(0, 0, 0, 0),
                ),
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP,
            )
        }
        canvas.drawCircle(offsetX, offsetY, min(width, height) * 0.2f, glowPaint)
    }

    private fun renderCyberGrid(
        canvas: Canvas,
        width: Int,
        height: Int,
        tiltX: Float,
        tiltY: Float,
    ) {
        canvas.drawColor(Color.parseColor("#030712"))

        val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(110, 56, 189, 248)
            strokeWidth = 2f
        }

        val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#A3E635")
            strokeWidth = 3f
        }

        for (x in 0..width step 54) {
            val offset = tiltX * 90f * (x / 54f % 2f - 0.5f)
            val path = Path().apply {
                moveTo(x + offset, 0f)
                lineTo(x + offset + tiltY * 150f, height.toFloat())
            }
            canvas.drawPath(path, gridPaint)
        }

        for (y in 0..height step 54) {
            val offset = tiltY * 90f * (y / 54f % 2f - 0.5f)
            val path = Path().apply {
                moveTo(0f, y + offset)
                lineTo(width.toFloat(), y + offset + tiltX * 150f)
            }
            canvas.drawPath(path, gridPaint)
        }

        for (i in 0..12) {
            val x = width * (i / 12f + 0.1f)
            val y = height * (0.5f + sin(i * 1.5f + tiltX * 4f) * 0.25f)
            canvas.drawLine(x, 0f, x + tiltX * 120f, height.toFloat(), accentPaint)
            canvas.drawLine(0f, y, width.toFloat(), y + tiltY * 120f, accentPaint)
        }
    }

    class RemoteWallpaperParallaxRenderer {
        private val maxParallaxPixels = 28f
        private val updateThreshold = 0.012f
        private var sourcePixels = IntArray(0)
        private var depthPixels = IntArray(0)
        private var outputPixels = IntArray(0)
        private var outputBitmap: Bitmap? = null
        private var renderedImage: Bitmap? = null
        private var renderedDepth: Bitmap? = null
        private var imageWidth = 0
        private var imageHeight = 0
        private var depthWidth = 0
        private var depthHeight = 0
        private var lastTiltX = Float.NaN
        private var lastTiltY = Float.NaN

        fun render(
            canvas: Canvas,
            width: Int,
            height: Int,
            tiltX: Float,
            tiltY: Float,
            assets: RemoteWallpaperAssets,
        ) {
            if (renderedImage !== assets.image ||
                renderedDepth !== assets.depth ||
                imageWidth != assets.image.width ||
                imageHeight != assets.image.height ||
                depthWidth != assets.depth.width ||
                depthHeight != assets.depth.height
            ) {
                imageWidth = assets.image.width
                imageHeight = assets.image.height
                depthWidth = assets.depth.width
                depthHeight = assets.depth.height
                sourcePixels = IntArray(imageWidth * imageHeight)
                depthPixels = IntArray(depthWidth * depthHeight)
                outputPixels = IntArray(imageWidth * imageHeight)
                assets.image.getPixels(sourcePixels, 0, imageWidth, 0, 0, imageWidth, imageHeight)
                assets.depth.getPixels(depthPixels, 0, depthWidth, 0, 0, depthWidth, depthHeight)
                outputBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
                renderedImage = assets.image
                renderedDepth = assets.depth
                lastTiltX = Float.NaN
                lastTiltY = Float.NaN
            }

            if (lastTiltX.isNaN() ||
                kotlin.math.abs(tiltX - lastTiltX) >= updateThreshold ||
                kotlin.math.abs(tiltY - lastTiltY) >= updateThreshold
            ) {
                warpPixels(tiltX, tiltY)
                outputBitmap?.setPixels(outputPixels, 0, imageWidth, 0, 0, imageWidth, imageHeight)
                lastTiltX = tiltX
                lastTiltY = tiltY
            }

            val scale = maxOf(width / imageWidth.toFloat(), height / imageHeight.toFloat()) * 1.08f
            val scaledWidth = imageWidth * scale
            val scaledHeight = imageHeight * scale
            canvas.drawBitmap(
                outputBitmap ?: assets.image,
                null,
                RectF(
                    (width - scaledWidth) / 2f,
                    (height - scaledHeight) / 2f,
                    (width + scaledWidth) / 2f,
                    (height + scaledHeight) / 2f,
                ),
                Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG),
            )
        }

        private fun warpPixels(tiltX: Float, tiltY: Float) {
            for (y in 0 until imageHeight) {
                val depthY = (y * depthHeight / imageHeight).coerceAtMost(depthHeight - 1)
                for (x in 0 until imageWidth) {
                    val depthX = (x * depthWidth / imageWidth).coerceAtMost(depthWidth - 1)
                    val depthColor = depthPixels[depthY * depthWidth + depthX]
                    val brightness = (
                        (Color.red(depthColor) * 0.299f) +
                            (Color.green(depthColor) * 0.587f) +
                            (Color.blue(depthColor) * 0.114f)
                        ) / 255f
                    val sourceX = (x - tiltX * maxParallaxPixels * brightness)
                        .coerceIn(0f, (imageWidth - 1).toFloat())
                    val sourceY = (y - tiltY * maxParallaxPixels * brightness)
                        .coerceIn(0f, (imageHeight - 1).toFloat())
                    outputPixels[y * imageWidth + x] = sampleBilinear(sourceX, sourceY)
                }
            }
        }

        private fun sampleBilinear(x: Float, y: Float): Int {
            val left = floor(x).toInt()
            val top = floor(y).toInt()
            val right = (left + 1).coerceAtMost(imageWidth - 1)
            val bottom = (top + 1).coerceAtMost(imageHeight - 1)
            val horizontal = x - left
            val vertical = y - top
            val topLeft = sourcePixels[top * imageWidth + left]
            val topRight = sourcePixels[top * imageWidth + right]
            val bottomLeft = sourcePixels[bottom * imageWidth + left]
            val bottomRight = sourcePixels[bottom * imageWidth + right]
            return interpolateColor(topLeft, topRight, bottomLeft, bottomRight, horizontal, vertical)
        }

        private fun interpolateColor(
            topLeft: Int,
            topRight: Int,
            bottomLeft: Int,
            bottomRight: Int,
            horizontal: Float,
            vertical: Float,
        ): Int {
            fun channel(shift: Int): Int {
                val top = ((topLeft shr shift and 0xFF) * (1f - horizontal)) +
                    ((topRight shr shift and 0xFF) * horizontal)
                val bottom = ((bottomLeft shr shift and 0xFF) * (1f - horizontal)) +
                    ((bottomRight shr shift and 0xFF) * horizontal)
                return (top * (1f - vertical) + bottom * vertical).toInt().coerceIn(0, 255)
            }
            return (channel(24) shl 24) or (channel(16) shl 16) or (channel(8) shl 8) or channel(0)
        }
    }

    private fun renderAmoledQuantum(
        canvas: Canvas,
        width: Int,
        height: Int,
        tiltX: Float,
        tiltY: Float,
    ) {
        canvas.drawColor(Color.BLACK)

        val radius = min(width, height) * 0.18f
        val centerX = width * 0.5f + tiltX * width * 0.12f
        val centerY = height * 0.5f + tiltY * height * 0.12f

        for (i in 0 until 9) {
            val angle = i * 0.7f + tiltX * 2.5f
            val x = centerX + cos(angle) * radius * (1.6f + i * 0.5f)
            val y = centerY + sin(angle * 1.7f) * radius * (1.2f + i * 0.5f)
            val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 2.2f
                color = Color.argb(120, 59, 130, 246)
                maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL)
            }
            canvas.drawCircle(x, y, radius * (0.8f + i * 0.18f), ringPaint)
        }

        val glow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                centerX,
                centerY,
                radius * 2.6f,
                intArrayOf(
                    Color.argb(70, 96, 165, 250),
                    Color.argb(25, 96, 165, 250),
                    Color.argb(0, 0, 0, 0),
                ),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP,
            )
        }
        canvas.drawCircle(centerX, centerY, radius * 2.5f, glow)
    }
}
