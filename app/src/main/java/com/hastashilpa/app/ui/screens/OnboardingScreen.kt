package com.hastashilpa.app.ui.screens
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


private data class OnboardPage(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val bg: List<Color>
)

@Composable
fun OnboardingScreen(
    navController: NavHostController,
    onFinish: () -> Unit          // called when user taps "Get Started"
) {
    val pages = listOf(
        OnboardPage(
            emoji    = "🎋",
            title    = "Discover Modern Designs",
            subtitle = "Browse trending bamboo & cane products loved by urban buyers. Stay ahead of the market.",
            bg       = listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))
        ),
        OnboardPage(
            emoji    = "📐",
            title    = "Build with Blueprints",
            subtitle = "Step-by-step construction guides with precise cm/mm measurements. Download PDFs offline.",
            bg       = listOf(Color(0xFFE65100), Color(0xFFBF360C))
        ),
        OnboardPage(
            emoji    = "💰",
            title    = "Price & Sell Smarter",
            subtitle = "Calculate fair selling prices, track materials, and list your products on the marketplace.",
            bg       = listOf(Color(0xFF1565C0), Color(0xFF0D47A1))
        )
    )

    var currentPage by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(pages[currentPage].bg))
    ) {
        Column(
            modifier            = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Skip button
            Row(Modifier.fillMaxWidth(), Arrangement.End) {
                if (currentPage < pages.size - 1) {
                    val skipSrc = remember { MutableInteractionSource() }
                    Text(
                        "Skip",
                        color      = Color(0xBFFFFFFF),
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier   = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(skipSrc, null) { onFinish() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Centre content
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(pages[currentPage].emoji, fontSize = 96.sp)
                Spacer(Modifier.height(32.dp))
                Text(
                    pages[currentPage].title,
                    fontSize    = 26.sp,
                    fontWeight  = FontWeight.Black,
                    color       = Color.White,
                    textAlign   = TextAlign.Center,
                    lineHeight  = 32.sp,
                    letterSpacing = (-0.5).sp
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    pages[currentPage].subtitle,
                    fontSize   = 14.sp,
                    color      = Color(0xBFFFFFFF),
                    textAlign  = TextAlign.Center,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Dots + button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Dot indicators
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pages.indices.forEach { i ->
                        val isActive = i == currentPage
                        val width by animateFloatAsState(
                            targetValue   = if (isActive) 24f else 8f,
                            animationSpec = tween(250),
                            label         = "dot$i"
                        )
                        Box(
                            modifier = Modifier
                                .size(width = width.dp, height = 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isActive) Color.White else Color(0x60FFFFFF)
                                )
                        )
                    }
                }
                Spacer(Modifier.height(28.dp))

                val btnSrc = remember { MutableInteractionSource() }
                val isLast = currentPage == pages.size - 1
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White)
                        .clickable(btnSrc, ripple(color = Color(0x30000000))) {
                            if (isLast) onFinish()
                            else currentPage++
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isLast) "Get Started 🚀" else "Next →",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Black,
                        color      = pages[currentPage].bg.first()
                    )
                }
            }
        }
    }
}