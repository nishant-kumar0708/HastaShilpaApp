package com.hastashilpa.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hastashilpa.app.viewmodel.GenAiViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.gestures.transformable

// ── Design tokens (mirror Screens.kt tokens — same package, no import needed)
private val GenPrimary   = Color(0xFF2E7D32)
private val GenAccent    = Color(0xFFE65100)
private val GenBg        = Color(0xFFF5F5F5)
private val GenSurface   = Color(0xFFFAFAFA)
private val GenBorder    = Color(0xFFEEEEEE)
private val GenTextHigh  = Color(0xFF1A1A1A)
private val GenTextMed   = Color(0xFF424242)
private val GenTextLow   = Color(0xFF9E9E9E)
private val GenTextWhite = Color(0xFFFFFFFF)
private val GenPrimaryXL = Color(0xFFE8F5E9)
private val GenAccentXL  = Color(0xFFFFF3E0)
private val GenAiPurple  = Color(0xFF6A1B9A)
private val GenAiPurpleXL = Color(0xFFF3E5F5)
private val GenCardShape = RoundedCornerShape(20.dp)
private val GenChipShape = RoundedCornerShape(50.dp)
private val GenBtnShape  = RoundedCornerShape(50.dp)

// ════════════════════════════════════════════════════════════════════════════
// GEN AI DESIGN SUGGESTER SCREEN
// ════════════════════════════════════════════════════════════════════════════

/**
 * GenAiDesignScreen — calls the Gemini API via [GenAiViewModel] to generate
 * 3 modern bamboo/cane product design variations for a given product type.
 *
 * This is a BRAND NEW screen. No existing files are modified.
 * Route: Screen.GenAi.route  (add this to Screen.kt and AppNavGraph.kt — see instructions)
 */
@Composable
fun GenAiDesignScreen(navController: NavHostController) {

    val vm: GenAiViewModel = viewModel()

    val productType by vm.productType.collectAsState()
    val isLoading   by vm.isLoading.collectAsState()
    val error       by vm.error.collectAsState()
    val suggestions by vm.suggestions.collectAsState()

    Column(Modifier.fillMaxSize().background(GenBg)) {

        // ── Top bar ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF4A148C), GenAiPurple)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val backSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x1FFFFFFF))
                    .clickable(backSource, ripple(color = Color.White)) {
                        navController.popBackStack()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("←", color = GenTextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Text("  ✨", fontSize = 20.sp)
            Text(
                "  AI Design Suggester",
                color = GenTextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.3).sp
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // ── AI badge ─────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .clip(GenChipShape)
                    .background(GenAiPurpleXL)
                    .border(1.dp, GenAiPurple.copy(alpha = 0.3f), GenChipShape)
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("⚡", fontSize = 13.sp)
                Text(
                    "Powered by Gemini AI",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GenAiPurple
                )
            }
            Spacer(Modifier.height(12.dp))

            Text(
                "What product do you want to design?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = GenTextHigh,
                lineHeight = 22.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Enter a bamboo or cane product type and our AI will suggest 3 modern design variations with dimensions and market tips.",
                fontSize = 13.sp,
                color = GenTextMed,
                lineHeight = 19.sp
            )
            Spacer(Modifier.height(20.dp))

            // ── Input card ────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth().shadow(4.dp, GenCardShape),
                shape    = GenCardShape,
                colors   = CardDefaults.cardColors(containerColor = GenSurface)
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "PRODUCT TYPE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GenTextLow,
                        letterSpacing = 1.sp
                    )
                    OutlinedTextField(
                        value         = productType,
                        onValueChange = { vm.updateProductType(it) },
                        placeholder   = {
                            Text(
                                "e.g. Bamboo Chair, Cane Basket, Lamp…",
                                fontSize = 13.sp,
                                color = GenTextLow
                            )
                        },
                        modifier        = Modifier.fillMaxWidth(),
                        shape           = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors          = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = GenAiPurple,
                            unfocusedBorderColor = GenBorder,
                            focusedTextColor     = GenTextHigh,
                            unfocusedTextColor   = GenTextHigh
                        ),
                        singleLine = true
                    )

                    // ── Quick-pick chips ──────────────────────────────────
                    Text(
                        "QUICK PICKS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GenTextLow,
                        letterSpacing = 1.sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Lamp", "Chair", "Basket").forEach { pick ->
                            QuickPickChip(pick) { vm.updateProductType(pick) }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Shelf", "Tray", "Headboard").forEach { pick ->
                            QuickPickChip(pick) { vm.updateProductType(pick) }
                        }
                    }

                    // ── Generate button ───────────────────────────────────
                    val generateSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(GenBtnShape)
                            .background(
                                if (isLoading)
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF9C27B0).copy(alpha = 0.5f),
                                            Color(0xFF9C27B0).copy(alpha = 0.5f)
                                        )
                                    )
                                else
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF6A1B9A), Color(0xFF9C27B0))
                                    )
                            )
                            .clickable(generateSource, ripple(color = Color.White)) {
                                if (!isLoading) vm.generateDesigns()
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color    = GenTextWhite,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    "Generating designs…",
                                    color = GenTextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        } else {
                            Text(
                                "✨ Generate 3 Design Variations",
                                color = GenTextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // ── Error state ───────────────────────────────────────────────
            error?.let { errMsg ->
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFEBEE))
                        .border(1.dp, Color(0xFFEF9A9A), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        "⚠️ $errMsg",
                        fontSize = 13.sp,
                        color = Color(0xFFC62828),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ── Results ───────────────────────────────────────────────────
            if (suggestions.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                Text(
                    "AI DESIGN SUGGESTIONS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GenTextLow,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "3 modern variations for \"$productType\"",
                    fontSize = 13.sp,
                    color = GenAiPurple,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(12.dp))

                suggestions.forEachIndexed { index, design ->
                    DesignSuggestionCard(
                        index  = index + 1,
                        design = design
                    )
                    Spacer(Modifier.height(12.dp))
                }

                // ── Disclaimer ────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GenAiPurpleXL)
                        .padding(14.dp)
                ) {
                    Text(
                        "💡 These designs are AI-generated suggestions. Always validate dimensions with a master artisan before production.",
                        fontSize = 11.sp,
                        color = GenAiPurple,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 16.sp
                    )
                }
            }

            // ── Empty / intro state ───────────────────────────────────────
            if (suggestions.isEmpty() && !isLoading && error == null) {
                Spacer(Modifier.height(32.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✨", fontSize = 56.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Enter a product type above",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GenTextMed,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Gemini AI will suggest 3 modern design variations with materials, dimensions, and pricing tips",
                        fontSize = 13.sp,
                        color = GenTextLow,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════

@Composable
fun DesignInfoRow(icon: String, label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment     = Alignment.Top
    ) {
        Text(icon, fontSize = 15.sp)
        Column {
            Text(
                label,
                fontSize      = 10.sp,
                fontWeight    = FontWeight.ExtraBold,
                color         = GenTextLow,
                letterSpacing = 0.8.sp
            )
            Text(
                value,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = GenTextHigh,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun QuickPickChip(label: String, onClick: () -> Unit) {
    val src = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .clip(GenChipShape)
            .background(GenAiPurpleXL)
            .border(1.dp, GenAiPurple.copy(alpha = 0.3f), GenChipShape)
            .clickable(src, ripple()) { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            label,
            fontSize   = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = GenAiPurple
        )
    }
}
@Composable
fun DesignSuggestionCard(index: Int, design: com.hastashilpa.app.viewmodel.DesignSuggestion) {

    // ── KEY FIX FOR SAME IMAGES ────────────────────────────────────────────
    // All 3 cards may share a similar product type (e.g. "lamp")
    // Using (index - 1) as the Unsplash result index means:
    //   Card 1 → results[0] from API → photo A
    //   Card 2 → results[1] from API → photo B
    //   Card 3 → results[2] from API → photo C
    // Since we request 10 results per query, each card gets a UNIQUE photo
    val query = com.hastashilpa.app.util.ProductImageUrls.getAiDesignQuery(
        designName = design.name,
        style      = design.style
    )
    val photoIndex = index - 1  // index is 1,2,3 → photoIndex is 0,1,2

    // ── Zoom state ─────────────────────────────────────────────────────────
    var scale   by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val transformState = androidx.compose.foundation.gestures.rememberTransformableState {
            zoomChange, panChange, _ ->
        scale   = (scale * zoomChange).coerceIn(1f, 4f)
        offsetX = (offsetX + panChange.x).coerceIn(-300f * (scale - 1f), 300f * (scale - 1f))
        offsetY = (offsetY + panChange.y).coerceIn(-300f * (scale - 1f), 300f * (scale - 1f))
    }

    Card(
        modifier = Modifier.fillMaxWidth().shadow(6.dp, GenCardShape),
        shape    = GenCardShape,
        colors   = CardDefaults.cardColors(containerColor = GenSurface)
    ) {
        Column {
            // ── Header ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF4A148C).copy(alpha = 0.85f),
                                Color(0xFF6A1B9A).copy(alpha = 0.85f)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp).clip(CircleShape)
                            .background(Color(0x33FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(index.toString(), color = GenTextWhite,
                            fontSize = 14.sp, fontWeight = FontWeight.Black)
                    }
                    Column {
                        Text(design.name, color = GenTextWhite,
                            fontSize = 15.sp, fontWeight = FontWeight.Black,
                            lineHeight = 18.sp)
                        Text(design.style, color = Color(0xCCFFFFFF),
                            fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Column(Modifier.padding(16.dp)) {

                // ── Zoomable image with unique index ──────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GenAiPurpleXL)
                        .transformable(state = transformState)

                ) {
                    com.hastashilpa.app.util.UnsplashImage(
                        query        = query,
                        index        = photoIndex,   // ← UNIQUE per card
                        modifier     = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX       = scale,
                                scaleY       = scale,
                                translationX = offsetX,
                                translationY = offsetY
                            ),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        placeholder  = {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(GenAiPurpleXL),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    modifier    = Modifier.size(28.dp),
                                    color       = GenAiPurple,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth().height(60.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xBB000000))
                                )
                            )
                    )

                    // Style badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart).padding(10.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xCC4A148C))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("${design.emoji}  ${design.style}",
                            fontSize = 11.sp, color = Color.White,
                            fontWeight = FontWeight.Bold)
                    }

                    // Zoom hint
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd).padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x99000000))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text("🔍 pinch zoom", fontSize = 8.sp, color = Color.White)
                    }
                }

                Spacer(Modifier.height(14.dp))
                Text(design.description, fontSize = 13.sp,
                    color = GenTextMed, lineHeight = 19.sp)
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = GenBorder)
                Spacer(Modifier.height(12.dp))
                DesignInfoRow("📏", "Dimensions", design.dimensions)
                Spacer(Modifier.height(8.dp))
                DesignInfoRow("🪵", "Materials",  design.materials)
                Spacer(Modifier.height(8.dp))
                DesignInfoRow("⏱️", "Est. Time",  design.estimatedTime)
                Spacer(Modifier.height(8.dp))
                DesignInfoRow("💰", "Price Range", design.priceRange)
                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(GenPrimaryXL).padding(10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment     = Alignment.Top
                    ) {
                        Text("📈", fontSize = 14.sp)
                        Text(design.marketTip, fontSize = 12.sp,
                            color = GenPrimary, fontWeight = FontWeight.SemiBold,
                            lineHeight = 17.sp)
                    }
                }
            }
        }
    }
}