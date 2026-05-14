package com.hastashilpa.app.util
import android.content.Context
import java.io.File

object OfflineCache {

    // Save any text content (blueprint steps, product data) to cache
    fun saveText(context: Context, filename: String, content: String) {
        val file = File(context.cacheDir, filename)
        file.writeText(content)
    }

    // Read cached text content
    fun readText(context: Context, filename: String): String? {
        val file = File(context.cacheDir, filename)
        return if (file.exists()) file.readText() else null
    }

    // Check if a cached file exists
    fun exists(context: Context, filename: String): Boolean {
        return File(context.cacheDir, filename).exists()
    }

    // Clear all cache
    fun clearAll(context: Context) {
        context.cacheDir.deleteRecursively()
    }
}