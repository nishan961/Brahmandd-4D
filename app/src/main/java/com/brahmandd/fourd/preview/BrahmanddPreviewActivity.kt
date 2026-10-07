package com.brahmandd.fourd.preview

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.brahmandd.fourd.data.WallpaperStateManager

class BrahmanddPreviewActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stateManager = WallpaperStateManager(this)
        val previewView = BrahmanddPreviewView(this, stateManager)
        setContentView(previewView)
    }
}
