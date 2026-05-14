package com.hastashilpa.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class DesignSuggestion(
    val name         : String,
    val style        : String,
    val emoji        : String,
    val description  : String,
    val dimensions   : String,
    val materials    : String,
    val estimatedTime: String,
    val priceRange   : String,
    val marketTip    : String
)

class GenAiViewModel : ViewModel() {

    private val GEMINI_API_KEY = com.hastashilpa.app.BuildConfig.GEMINI_KEY

    //USING GEMINI 2.5 FLASH
    private val GEMINI_ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$GEMINI_API_KEY"

    val productType          = MutableStateFlow("")
    private val _isLoading   = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>                  = _isLoading
    private val _error       = MutableStateFlow<String?>(null)
    val error: StateFlow<String?>                      = _error
    private val _suggestions = MutableStateFlow<List<DesignSuggestion>>(emptyList())
    val suggestions: StateFlow<List<DesignSuggestion>> = _suggestions

    fun updateProductType(value: String) {
        productType.value = value
        _error.value      = null
    }

    fun generateDesigns() {
        val input = productType.value.trim()
        if (input.isBlank()) {
            _error.value = "Please enter a product type first."
            return
        }

        viewModelScope.launch {
            _isLoading.value   = true
            _error.value       = null
            _suggestions.value = emptyList()

            try {
                val raw    = withContext(Dispatchers.IO) { callApi(input) }
                val parsed = parseResponse(raw)
                if (parsed.isNotEmpty()) {
                    _suggestions.value = parsed
                } else {
                    _error.value = "No designs generated. Please try again."
                }
            } catch (e: Exception) {
                _error.value = when {
                    e.message?.contains("401") == true ->
                        "Invalid API key. Check your Gemini API key."
                    e.message?.contains("429") == true ->
                        "Rate limit reached. Wait a moment and try again."
                    e.message?.contains("UnknownHost") == true ->
                        "No internet connection."
                    else -> "Error: ${e.message?.take(120)}"
                }
            }

            _isLoading.value = false
        }
    }

    private fun callApi(productType: String): String {
        val body = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", buildPrompt(productType))
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("maxOutputTokens", 2000)
                // Force JSON output — prevents markdown wrapping
                put("responseMimeType", "application/json")
            })
        }

        val conn = (URL(GEMINI_ENDPOINT).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            doOutput      = true
            connectTimeout = 20_000
            readTimeout    = 40_000
        }

        OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }

        val code = conn.responseCode
        if (code != 200) {
            val err = conn.errorStream?.bufferedReader()?.readText() ?: "no error body"
            throw Exception("HTTP $code: $err")
        }

        return BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
    }

    private fun buildPrompt(productType: String) = """
You are a master craftsman and design expert specializing in bamboo and cane handicrafts for urban Indian markets.

Generate exactly 3 unique design variations for a "$productType" made from bamboo or cane materials.

Return ONLY a JSON array with exactly 3 objects. Each object must have these exact fields:
- name: creative product name (string)
- style: design style like "Modern Minimalist" or "Bohemian" (string)
- emoji: single relevant emoji (string)
- description: 2 sentence description of the design (string)
- dimensions: specific measurements like "45 x 30 x 60 cm" (string)
- materials: list of materials like "Bamboo poles 25mm, Cane strips 6mm" (string)
- estimatedTime: craft time like "3-4 days" (string)
- priceRange: selling price like "₹1200 - ₹1800" (string)
- marketTip: one sentence urban market selling tip (string)

Important: Return ONLY the JSON array. No markdown, no backticks, no explanation text whatsoever.
    """.trimIndent()

    private fun parseResponse(raw: String): List<DesignSuggestion> {
        // Step 1: Extract text from Gemini response envelope
        val candidates = JSONObject(raw).getJSONArray("candidates")
        val content    = candidates.getJSONObject(0).getJSONObject("content")
        val parts      = content.getJSONArray("parts")
        var text       = parts.getJSONObject(0).getString("text").trim()

        // Step 2: Strip any markdown fences Gemini might add despite responseMimeType
        text = text
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        // Step 3: Find JSON array boundaries — handles cases where text
        // has extra content before/after the array
        val arrayStart = text.indexOf('[')
        val arrayEnd   = text.lastIndexOf(']')

        // Step 4: If no array, try treating whole response as JSON object
        // and wrap it, or extract from object with array field
        val jsonText = when {
            arrayStart != -1 && arrayEnd != -1 ->
                text.substring(arrayStart, arrayEnd + 1)
            text.startsWith("{") -> {
                // Gemini returned single object — check if it has a nested array
                val obj = JSONObject(text)
                val key = obj.keys().asSequence()
                    .firstOrNull { obj.opt(it) is JSONArray }
                if (key != null) obj.getJSONArray(key).toString()
                else "[$text]"   // wrap single object as array
            }
            else -> throw Exception("Unexpected response format: ${text.take(100)}")
        }

        // Step 5: Parse array and build DesignSuggestion list
        val arr = JSONArray(jsonText)
        return (0 until minOf(arr.length(), 3)).mapNotNull { i ->
            try {
                val o = arr.getJSONObject(i)
                DesignSuggestion(
                    name          = o.optString("name",          "Design ${i + 1}"),
                    style         = o.optString("style",         "Artisan"),
                    emoji         = o.optString("emoji",         "🎋"),
                    description   = o.optString("description",   "A beautiful bamboo design."),
                    dimensions    = o.optString("dimensions",    "Standard size"),
                    materials     = o.optString("materials",     "Bamboo, Cane"),
                    estimatedTime = o.optString("estimatedTime", "2-3 days"),
                    priceRange    = o.optString("priceRange",    "₹500 - ₹1500"),
                    marketTip     = o.optString("marketTip",     "Sell at local craft markets.")
                )
            } catch (e: Exception) {
                null  // skip malformed items, don't crash whole list
            }
        }
    }
}