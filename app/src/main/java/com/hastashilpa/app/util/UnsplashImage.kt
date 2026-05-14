package com.hastashilpa.app.util
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

/**
 * Reusable composable that fetches the correct Unsplash image
 * for any search query and displays it via Coil.
 *
 * Usage:
 *   UnsplashImage(
 *       query    = "rattan chair",
 *       index    = 0,
 *       modifier = Modifier.fillMaxSize()
 *   )
 */
@Composable
fun UnsplashImage(
    query       : String,
    index       : Int      = 0,
    modifier    : Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder : @Composable () -> Unit = {
        Box(modifier.background(Color(0xFFE8F0E8)))
    }
) {
    var imageUrl by remember(query, index) { mutableStateOf<String?>(null) }
    val scope    = rememberCoroutineScope()

    LaunchedEffect(query, index) {
        scope.launch {
            imageUrl = UnsplashImageFetcher.fetchUrl(query, index)
        }
    }

    if (imageUrl != null) {
        AsyncImage(
            model              = imageUrl,
            contentDescription = query,
            contentScale       = contentScale,
            modifier           = modifier
        )
    } else {
        placeholder()
    }
}