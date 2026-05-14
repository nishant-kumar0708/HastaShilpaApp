package com.hastashilpa.app
import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache

class HastaShilpaApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .memoryCache {
                    MemoryCache.Builder(this)
                        .maxSizePercent(0.25)   // 25% of available RAM
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(cacheDir.resolve("hs_image_cache"))
                        .maxSizeBytes(60L * 1024 * 1024)   // 60 MB disk cache
                        .build()
                }
                .crossfade(true)
                .build()
        )
    }
}