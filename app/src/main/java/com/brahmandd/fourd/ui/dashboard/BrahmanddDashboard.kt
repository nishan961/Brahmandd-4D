package com.brahmandd.fourd.ui.dashboard

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brahmandd.fourd.data.RemoteWallpaper
import com.brahmandd.fourd.data.WallpaperCategory

@Composable
fun BrahmanddDashboard(
    selectedCategory: WallpaperCategory,
    remoteWallpapers: List<RemoteWallpaper>,
    remoteWallpaperImages: Map<String, Bitmap>,
    selectedRemoteWallpaperId: String?,
    onCategorySelected: (WallpaperCategory) -> Unit,
    onRemoteWallpaperSelected: (RemoteWallpaper) -> Unit,
    onApplyWallpaper: () -> Unit,
    hasCustomShivaImage: Boolean,
    onSelectShivaImage: () -> Unit,
    onPreview: () -> Unit,
) {
    val categories = WallpaperCategory.entries

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030712),
                        Color(0xFF0F172A),
                        Color(0xFF111827),
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "BRAHMANDD 4D",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "Adaptive live wallpaper ecosystem",
                color = Color(0xFFCBD5E1),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onCategorySelected(category) },
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) Color(category.accentStart) else Color(0xFF1E293B),
                        tonalElevation = if (isSelected) 8.dp else 0.dp,
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = category.label,
                                color = if (isSelected) Color.White else Color(0xFFE2E8F0),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                            )
                        }
                    }
                }
                items(remoteWallpapers, key = { it.id }) { wallpaper ->
                    val isSelected = wallpaper.id == selectedRemoteWallpaperId
                    Card(
                        modifier = Modifier
                            .width(156.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onRemoteWallpaperSelected(wallpaper) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF334155) else Color(0xFF1E293B),
                        ),
                        shape = RoundedCornerShape(18.dp),
                    ) {
                        Column {
                            val art = remoteWallpaperImages[wallpaper.id]
                            if (art != null) {
                                Image(
                                    bitmap = art.asImageBitmap(),
                                    contentDescription = wallpaper.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(96.dp),
                                    contentScale = ContentScale.Crop,
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(96.dp)
                                        .background(Color(0xFF0F172A)),
                                )
                            }
                            Text(
                                text = wallpaper.name,
                                color = Color(0xFFE2E8F0),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            )
                        }
                    }
                }
            }
            val selectedRemoteWallpaper = remoteWallpapers.firstOrNull {
                it.id == selectedRemoteWallpaperId
            }
            if (selectedRemoteWallpaper != null) {
                Text(
                    text = "Selected: ${selectedRemoteWallpaper.name}. Apply or preview to use its depth-based gyro parallax.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp,
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0x401E293B)),
                shape = RoundedCornerShape(28.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(
                            Brush.linearGradient(
                                colors = if (selectedRemoteWallpaper == null) {
                                    listOf(
                                        Color(selectedCategory.accentStart),
                                        Color(selectedCategory.accentEnd),
                                        Color(0xFF0F172A),
                                    )
                                } else {
                                    listOf(Color(0xFF312E81), Color(0xFF0F172A))
                                }
                            )
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.BottomStart,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = selectedRemoteWallpaper?.name ?: selectedCategory.label,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                        )
                        Text(
                            text = selectedRemoteWallpaper?.description?.takeIf { it.isNotBlank() }
                                ?: selectedCategory.description,
                            color = Color(0xFFDDEAFE),
                            fontSize = 13.sp,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = onApplyWallpaper,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF38BDF8),
                        contentColor = Color(0xFF020817),
                    ),
                ) {
                    Text(text = "Apply Wallpaper")
                }

                OutlinedButton(
                    onClick = onPreview,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = "Preview")
                }
            }

            OutlinedButton(
                onClick = onSelectShivaImage,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = if (hasCustomShivaImage) "Change Shiva Photo" else "Upload Shiva Photo")
            }

            Text(
                text = if (hasCustomShivaImage) {
                    "Your photo is ready. Choose Shiva • Kailash, then apply the wallpaper."
                } else {
                    "Choose a photo from your device to use in the Shiva • Kailash parallax wallpaper."
                },
                color = Color(0xFFCBD5E1),
                fontSize = 12.sp,
            )

        }
    }
}
