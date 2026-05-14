package com.hastashilpa.app.util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Fetches real Unsplash images via API.
 * Requests 10 results per query so different index = different photo.
 */
object UnsplashImageFetcher {

    private val ACCESS_KEY = com.hastashilpa.app.BuildConfig.UNSPLASH_KEY
    private const val BASE_URL   = "https://api.unsplash.com/search/photos"

    // Cache: key = "query|index" → image URL
    private val cache = mutableMapOf<String, String>()

    /**
     * Fetch a specific result by index from a search query.
     * index 0 = first result, index 1 = second result, etc.
     * Same query with different index = different photo guaranteed.
     */
    suspend fun fetchUrl(query: String, index: Int = 0): String? {
        val cacheKey = "$query|$index"
        cache[cacheKey]?.let { return it }

        return withContext(Dispatchers.IO) {
            try {
                val encoded  = URLEncoder.encode(query, "UTF-8")
                // Request 10 results so index 0-9 all return different photos
                val url = "$BASE_URL?query=$encoded&per_page=10&orientation=squarish&client_id=$ACCESS_KEY"

                val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                    requestMethod  = "GET"
                    connectTimeout = 10_000
                    readTimeout    = 10_000
                }
                if (conn.responseCode != 200) return@withContext null

                val json    = conn.inputStream.bufferedReader().readText()
                val results = JSONObject(json).getJSONArray("results")
                if (results.length() == 0) return@withContext null

                // Use modulo so index never exceeds available results
                val safeIndex = index % results.length()
                val imageUrl  = results
                    .getJSONObject(safeIndex)
                    .getJSONObject("urls")
                    .getString("regular")

                cache[cacheKey] = imageUrl
                imageUrl
            } catch (e: Exception) {
                null
            }
        }
    }
}