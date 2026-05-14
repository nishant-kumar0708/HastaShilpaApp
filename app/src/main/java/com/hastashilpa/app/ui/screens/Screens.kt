package com.hastashilpa.app.ui.screens
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hastashilpa.app.util.OfflineCache
import com.hastashilpa.app.util.PdfExporter
import com.hastashilpa.app.viewmodel.MarketplaceViewModel
import com.hastashilpa.app.viewmodel.PricerViewModel
import com.hastashilpa.app.viewmodel.TrackerViewModel
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.material3.CircularProgressIndicator
import com.hastashilpa.app.util.UnsplashImage
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.layout.ContentScale
import com.hastashilpa.app.data.BatchEntity

private val Primary      = Color(0xFF2E7D32)
private val PrimaryXL    = Color(0xFFE8F5E9)
private val Accent       = Color(0xFFE65100)
private val AccentXL     = Color(0xFFFFF3E0)
private val BgColor      = Color(0xFFF5F5F5)
private val SurfaceColor = Color(0xFFFAFAFA)
private val BorderColor  = Color(0xFFEEEEEE)


private val TextHigh     = Color(0xFF1A1A1A)
private val TextMed      = Color(0xFF424242)
private val TextLow      = Color(0xFF9E9E9E)
private val TextWhite    = Color(0xFFFFFFFF)
private val CardShape    = RoundedCornerShape(20.dp)
private val ChipShape    = RoundedCornerShape(50.dp)
private val BtnShape     = RoundedCornerShape(50.dp)

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

data class Blueprint(
    val name: String,
    val emoji: String,
    val dims: String,
    val material: String,
    val steps: List<String>
)

data class BatchEntry(
    val product: String,
    val poles: Int,
    val strips: Int,
    val date: String
)

data class PriceResult(
    val material: Double,
    val labour: Double,
    val base: Double,
    val margin: Double,
    val suggestedPrice: Double,
    val overheadPct: Double
)
// ════════════════════════════════════════════════════════════════════════════

// ════════════════════════════════════════════════════════════════════════════

// ════════════════════════════════════════════════════════════════════════════
// SCREEN SCAFFOLD
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun ScreenScaffold(
    title: String,
    emoji: String,
    navController: NavHostController,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(BgColor)
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF1B5E20), Primary)))
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
                Text("←", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Text(emoji, fontSize = 20.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                title,
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.3).sp
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            content = content
        )
    }
}

@Composable
fun BlueprintScreen(navController: NavHostController) {

    

    // ── Blueprint search queries for Unsplash API ─────────────────────────
    val blueprintQueries = mapOf(
        "Rattan Bloom Chair"    to  "wicker lounge chair natural",
        "Cane Storage Trunk"    to "handwoven rattan storage basket",
        "Woven Bamboo Lamp"     to "bamboo woven lamp shade",
        "Cane Dining Table"     to "cane rattan dining table",
        "Bamboo Bookshelf"      to "bamboo bookshelf natural wood",
        "Rattan Wall Mirror"    to "rattan wall mirror round",
        "Bamboo Planter Stand"  to "bamboo plant stand natural",
        "Cane Headboard"        to "cane rattan headboard bedroom",
        "Woven Cane Fruit Tray" to "woven cane fruit tray handmade",
        "Bamboo Ladder Shelf"   to "bamboo ladder shelf natural"
    )

    val blueprints = listOf(
        Blueprint(
            name     = "Rattan Bloom Chair",
            emoji    = "🪑",
            dims     = "60 × 45 × 90 cm",
            material = "Rattan core 6 mm · Cane webbing 4 mm · Binding cane 2 mm · Linseed oil",
            steps    = listOf(
                "Cut 4 legs to 45 cm each from 20 mm rattan pole. Taper bottom 5 cm for floor grip.",
                "Soak cane strips in warm water for 30 min to increase flexibility before weaving.",
                "Assemble seat frame 60 × 45 cm using mortise joints; lash corners with binding cane.",
                "Weave seat in diagonal herringbone pattern — 4 strips per 5 cm, alternating over-under.",
                "Attach back support at 110° recline angle; reinforce with double-lash knot.",
                "Weave backrest in open lattice pattern for airflow — 15 mm gap between strips.",
                "Sand all surfaces with 120-grit then 240-grit paper for smooth finish.",
                "Apply 2 coats raw linseed oil — allow 24 hrs drying time between coats."
            )
        ),
        Blueprint(
            name     = "Cane Storage Trunk",
            emoji    = "🧺",
            dims     = "50 × 35 × 30 cm",
            material = "Cane strips 6 mm · Bamboo frame 25 mm · Waterproof adhesive · Lacquer",
            steps    = listOf(
                "Cut bamboo frame: 2 × 50 cm (length), 4 × 35 cm (width), 4 × 30 cm (height uprights).",
                "Drill 8 mm holes at all joints; join with mortise & tenon secured with waterproof adhesive.",
                "Weave base panel using tight twill pattern — cane strips spaced every 8 mm.",
                "Build up side walls row by row, alternating 2-over-2-under pattern.",
                "Fold 10 mm binding cane around all top edges; stitch with whipping knot every 3 cm.",
                "Construct lid frame 52 × 37 cm (2 cm overlap each side); weave matching twill pattern.",
                "Attach 2 brass hinges 10 cm from each end; fix bamboo D-ring handle at centre.",
                "Apply 3 coats clear lacquer inside and out; sand lightly between coats."
            )
        ),
        Blueprint(
            name     = "Woven Bamboo Lamp",
            emoji    = "💡",
            dims     = "30 cm dia · 45 cm height",
            material = "Bamboo slats 10 × 2 mm · Galvanised wire 2 mm · E27 pendant holder · LED 8W",
            steps    = listOf(
                "Bend 2 mm galvanised wire into 30 cm diameter rings for top and bottom frames.",
                "Connect 8 vertical wire ribs at equal 45° spacing; solder all junctions.",
                "Soak 10 mm bamboo slats in water for 1 hour to prevent splitting during bending.",
                "Weave slats horizontally around ribs in continuous spiral from bottom to top.",
                "Maintain 5 mm gap between slats for warm light diffusion effect.",
                "Secure top and bottom rows with binding wire; tuck all loose ends inward.",
                "Thread pendant cable through top ring; attach E27 bakelite holder rated 60W max.",
                "Install 8W LED warm-white bulb (2700K) for best glow through bamboo weave."
            )
        ),
        Blueprint(
            name     = "Cane Dining Table",
            emoji    = "🍽️",
            dims     = "120 × 70 × 75 cm",
            material = "Bamboo poles 50 mm · Cane webbing 8 mm · Rattan wrapping · Glass top 6 mm",
            steps    = listOf(
                "Cut 4 bamboo leg poles to 73 cm each (accounts for 2 cm foot pad thickness).",
                "Cut 2 long frame rails 116 cm and 4 short rails 66 cm from 30 mm bamboo.",
                "Join all frame corners with double-pin bamboo joints; bind with soaked rattan strip.",
                "Add 2 central cross-braces at 37.5 cm from each end for rigidity.",
                "Weave cane webbing across tabletop frame in herringbone pattern — pull tight.",
                "Wrap all exposed bamboo poles in 6 mm rattan for refined look; secure ends.",
                "Sand all surfaces; apply 2 coats weatherproof varnish for indoor/outdoor use.",
                "Place 6 mm toughened glass top 120 × 70 cm with 4 clear rubber stoppers."
            )
        ),
        Blueprint(
            name     = "Bamboo Bookshelf",
            emoji    = "📚",
            dims     = "80 × 30 × 150 cm",
            material = "Bamboo poles 40 mm · Bamboo boards 15 mm · Rattan lashing · Tung oil",
            steps    = listOf(
                "Cut 4 vertical bamboo poles to 150 cm for uprights; flatten contact faces.",
                "Cut 5 shelf boards from 15 mm bamboo laminate: 80 × 30 cm each.",
                "Mark shelf positions at 0, 30, 65, 105, 150 cm from base.",
                "Drill 12 mm holes through uprights at shelf positions; insert shelf tenon ends.",
                "Lash all shelf-to-upright junctions with 3-layer diagonal rattan wrap.",
                "Add 3 mm bamboo back panel strips for stability — space 20 mm apart vertically.",
                "Level shelf on flat surface; check all angles are 90° before lashing dries.",
                "Finish with 2 coats tung oil — brings out natural bamboo grain and protects."
            )
        ),
        Blueprint(
            name     = "Rattan Wall Mirror",
            emoji    = "🪞",
            dims     = "60 cm diameter",
            material = "Rattan reed 4 mm · Binding cane 2 mm · 50 cm round mirror · D-ring mount",
            steps    = listOf(
                "Cut a 50 cm circular mirror or use standard size; set aside safely.",
                "Bend 4 mm rattan reed into a 58 cm diameter ring — overlap ends 10 cm and lash.",
                "Create inner ring at 52 cm diameter to grip mirror edge; line inside with felt strip.",
                "Weave radiating rattan spokes outward from inner ring — 24 spokes evenly spaced.",
                "Fill gaps between spokes with concentric rattan weave — 3 rows tightly packed.",
                "Build decorative sunburst fringe: 12 extended spokes protruding 4 cm beyond ring.",
                "Insert mirror into felt-lined inner ring; secure with 4 small rattan clips.",
                "Attach D-ring wall mount at 12 o'clock position; test load-bearing before hanging."
            )
        ),
        Blueprint(
            name     = "Bamboo Planter Stand",
            emoji    = "🌿",
            dims     = "35 × 35 × 60 cm",
            material = "Bamboo poles 25 mm · Cane webbing 4 mm · Waterproof sealant · Rubber feet",
            steps    = listOf(
                "Cut 4 leg poles to 58 cm; cut top frame 4 × 35 cm and mid-shelf frame 4 × 28 cm.",
                "Angle-cut leg tops at 5° outward flare for stability using mitre box.",
                "Join top frame corners with half-lap joints; secure with bamboo pins and adhesive.",
                "Attach legs to top frame at flared angle; lash with 3-wrap rattan binding.",
                "Add mid-shelf at 35 cm height for bottom tier; repeat lashing method.",
                "Weave cane webbing across both shelf frames — use over-under plain weave.",
                "Apply 2 coats waterproof sealant to protect against plant moisture and spills.",
                "Attach 4 rubber feet to leg bases; allows 5 mm airflow gap from floor."
            )
        ),
        Blueprint(
            name     = "Cane Headboard",
            emoji    = "🛏️",
            dims     = "150 × 80 cm",
            material = "Rattan poles 20 mm · Cane webbing 6 mm · Foam padding 20 mm · Fabric lining",
            steps    = listOf(
                "Build outer frame 150 × 80 cm using 20 mm rattan poles, mitred corners.",
                "Add 3 vertical dividers at 37.5 cm intervals creating 4 equal panels.",
                "Stretch cane webbing tightly across each panel; staple to back every 5 cm.",
                "Cut 20 mm foam to fit 2 centre panels; cover with fabric, staple to back.",
                "Weave decorative rattan border around all frame edges — use figure-8 lashing.",
                "Sand all exposed rattan; stain with walnut or natural finish as preferred.",
                "Drill 4 mounting holes at corners for wall-fixing with M8 bolts.",
                "Attach to wall studs at correct bed height; use spirit level to ensure horizontal."
            )
        ),
        Blueprint(
            name     = "Woven Cane Fruit Tray",
            emoji    = "🍊",
            dims     = "40 × 30 × 8 cm",
            material = "Cane stakes 4 mm · Cane weavers 2 mm · Seagrass border · Beeswax finish",
            steps    = listOf(
                "Cut 14 cane stakes to 60 cm length — they fold up to form the side walls.",
                "Lay 7 stakes horizontally, weave 7 stakes vertically — form 40 × 30 cm base grid.",
                "Tighten base to size; secure corners with clothes pegs while wet.",
                "Bend all stakes upward at 90° along base perimeter to form side walls.",
                "Weave 2 mm cane weavers around stakes in continuous plain weave for 8 cm height.",
                "Fold stake tips over last 2 rows; tuck under 3rd row from top for neat border.",
                "Bind top edge with twisted seagrass rope; stitch every 4 stakes with whipping.",
                "Apply beeswax polish with soft cloth; buff to light sheen — natural food-safe finish."
            )
        ),
        Blueprint(
            name     = "Bamboo Ladder Shelf",
            emoji    = "🪜",
            dims     = "50 × 35 × 160 cm",
            material = "Bamboo poles 35 mm · Bamboo dowels 15 mm · Jute rope 8 mm · Teak oil",
            steps    = listOf(
                "Cut 2 main ladder poles to 160 cm; mark 5 rung positions at 30, 60, 90, 120, 150 cm.",
                "Drill 16 mm holes at marked positions (slightly angled inward for stability).",
                "Cut 5 rungs from 35 mm bamboo: 3 at 50 cm (shelves) and 2 at 45 cm (rungs).",
                "Insert rungs into holes; secure with 15 mm bamboo dowel pins hammered through.",
                "Wrap 8 mm jute rope in X-pattern around each rung-to-pole joint — 6 wraps minimum.",
                "Cut 3 bamboo shelf boards 50 × 35 cm from 10 mm laminate; sand all edges smooth.",
                "Rest shelf boards on the 3 wide rungs — gravity holds them, no fixing needed.",
                "Finish all bamboo with 2 coats teak oil; protects and enhances natural grain."
            )
        )
    )

    var selectedIdx by remember { mutableStateOf(0) }
    val bp          = blueprints[selectedIdx]
    val context     = LocalContext.current

    // ── Zoom state for blueprint image — resets when blueprint changes ─────
    var scale   by remember(selectedIdx) { mutableStateOf(1f) }
    var offsetX by remember(selectedIdx) { mutableStateOf(0f) }
    var offsetY by remember(selectedIdx) { mutableStateOf(0f) }
    val transformState = androidx.compose.foundation.gestures.rememberTransformableState {
            zoomChange, panChange, _ ->
        scale   = (scale * zoomChange).coerceIn(1f, 5f)
        offsetX = (offsetX + panChange.x).coerceIn(-400f * (scale - 1f), 400f * (scale - 1f))
        offsetY = (offsetY + panChange.y).coerceIn(-300f * (scale - 1f), 300f * (scale - 1f))
    }

    ScreenScaffold("Blueprint Viewer", "📐", navController) {

        // ── Selector tabs horizontal scroll ──────────────────────────────
        Text("Select Design", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
            color = TextLow, letterSpacing = 1.sp)
        Spacer(Modifier.height(8.dp))

        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(blueprints.size) { idx ->
                val b    = blueprints[idx]
                val isOn = idx == selectedIdx
                val src  = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .clip(ChipShape)
                        .background(if (isOn) Primary else SurfaceColor)
                        .border(1.5.dp, if (isOn) Color.Transparent else BorderColor, ChipShape)
                        .clickable(src, ripple()) { selectedIdx = idx }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        b.emoji + " " + b.name.split(" ").first(),
                        fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
                        color = if (isOn) TextWhite else TextMed
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Blueprint card ────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, CardShape),
            shape    = CardShape,
            colors   = CardDefaults.cardColors(containerColor = SurfaceColor)
        ) {
            Column(Modifier.padding(16.dp)) {

                // ── Zoomable Unsplash image ───────────────────────────────
                val imageQuery = blueprintQueries[bp.name]
                    ?: "bamboo cane craft handmade"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0EBE0))
                        .background(Color.LightGray
                        )
                ) {
                    UnsplashImage(
                        query = imageQuery,
                        index = 0,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offsetX,
                                translationY = offsetY
                            ),
                        contentScale = ContentScale.Crop,
                        placeholder = {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFF0EBE0)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    color = Primary,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xCC000000))
                                )
                            )
                    )

                    // Dimension label
                    Text(
                        "📐 ${bp.dims}",
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp),
                        fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold
                    )

                    // Blueprint badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xCC1B5E20))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "BLUEPRINT", fontSize = 8.sp, color = Color.White,
                            fontWeight = FontWeight.Black, letterSpacing = 1.sp
                        )
                    }

                    // Zoom hint
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x99000000))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text("🔍 pinch to zoom", fontSize = 8.sp, color = Color.White)
                    }
                }

                Spacer(Modifier.height(14.dp))
                Text(bp.name, fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextHigh)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DimBadge("📏 ${bp.dims}")
                    DimBadge("🪵 ${bp.material.split("·").first().trim()}")
                }
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(Modifier.height(12.dp))
                Text("MATERIALS REQUIRED", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                    color = TextLow, letterSpacing = 1.sp)
                Spacer(Modifier.height(6.dp))
                bp.material.split("·").forEach { mat ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(Primary))
                        Text(mat.trim(), fontSize = 13.sp,
                            color = TextMed, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("CONSTRUCTION STEPS", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
            color = TextLow, letterSpacing = 1.sp)
        Spacer(Modifier.height(10.dp))
        bp.steps.forEachIndexed { i, step ->
            StepCard(stepNum = i + 1, text = step)
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(16.dp))

        // ── Download PDF ──────────────────────────────────────────────────
        val dlSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(BtnShape)
                .background(Primary)
                .clickable(dlSource, ripple(color = Color.White)) {
                    val file = PdfExporter.exportBlueprint(
                        context, bp.name, bp.dims, bp.material, bp.steps
                    )
                    Toast.makeText(
                        context,
                        if (file != null) "Saved to Downloads/${file.name}" else "Export failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("📄 Download Blueprint PDF", color = TextWhite,
                fontSize = 14.sp, fontWeight = FontWeight.Black)
        }

        Spacer(Modifier.height(10.dp))

        // ── Save offline ──────────────────────────────────────────────────
        var offlineSaved by remember {
            mutableStateOf(OfflineCache.exists(context, "${bp.name}.txt"))
        }
        val offlineSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(BtnShape)
                .background(if (offlineSaved) Color(0xFF9E9E9E) else Accent)
                .clickable(offlineSource, ripple(color = Color.White)) {
                    if (!offlineSaved) {
                        val content = buildString {
                            appendLine("BLUEPRINT: ${bp.name}")
                            appendLine("DIMENSIONS: ${bp.dims}")
                            appendLine("MATERIAL: ${bp.material}")
                            appendLine("---")
                            bp.steps.forEachIndexed { i, step -> appendLine("Step ${i + 1}: $step") }
                        }
                        OfflineCache.saveText(context, "${bp.name}.txt", content)
                        offlineSaved = true
                        Toast.makeText(context, "Saved offline: ${bp.name}", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(context, "Already saved offline ✓", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(if (offlineSaved) "✓ Saved for Offline" else "💾 Save for Offline",
                color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(20.dp))
    }
}

// ════════════════════════════════════════════════════════════════════════════
// 2. TRACKER SCREEN  ── now wired to TrackerViewModel + Export button added
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun TrackerScreen(
    navController: NavHostController,
    vm: TrackerViewModel = viewModel()           // ← ViewModel injected
) {
    val context = LocalContext.current

    // ── ViewModel state ──────────────────────────────────────────────────
    val persistedBatches by vm.batches.collectAsState()   // Room-backed list

    // ── Local UI state (form fields only) ────────────────────────────────
    var productName by remember { mutableStateOf("") }
    var bambooPoles by remember { mutableStateOf("") }
    var caneStrips  by remember { mutableStateOf("") }
    var date        by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    ScreenScaffold("Material Tracker", "🪵", navController) {

        Text(
            "LOG NEW BATCH",
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextLow,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, CardShape),
            shape    = CardShape,
            colors   = CardDefaults.cardColors(containerColor = SurfaceColor)
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TrackerField(
                    label         = "Product Name",
                    placeholder   = "e.g. Rattan Chair",
                    value         = productName,
                    keyboard      = KeyboardType.Text,
                    onValueChange = { productName = it }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        TrackerField(
                            label         = "Bamboo Poles",
                            placeholder   = "0",
                            value         = bambooPoles,
                            keyboard      = KeyboardType.Number,
                            onValueChange = { bambooPoles = it }
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        TrackerField(
                            label         = "Cane Strips",
                            placeholder   = "0",
                            value         = caneStrips,
                            keyboard      = KeyboardType.Number,
                            onValueChange = { caneStrips = it }
                        )
                    }
                }
                TrackerField(
                    label         = "Date (DD MMM YYYY)",
                    placeholder   = "01 May 2026",
                    value         = date,
                    keyboard      = KeyboardType.Text,
                    onValueChange = { date = it }
                )

                if (showSuccess) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryXL)
                            .padding(10.dp)
                    ) {
                        Text(
                            "✓ Batch logged and saved to database!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Primary
                        )
                    }
                }

                val saveSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(BtnShape)
                        .background(Accent)
                        .clickable(saveSource, ripple(color = Color.White)) {
                            if (productName.isNotBlank()) {
                                // ── Write to Room via ViewModel ────────────
                                vm.addBatch(
                                    product = productName,
                                    poles = bambooPoles.toIntOrNull() ?: 0,
                                    strips = caneStrips.toIntOrNull() ?: 0,
                                    date = date.ifBlank { "Today" }
                                )
                                // ── Reset form fields ──────────────────────
                                productName = ""
                                bambooPoles = ""
                                caneStrips = ""
                                date = ""
                                showSuccess = true
                            }
                        }
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Save Batch",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))

        // ── Export Batch Report button ─────────────────────────────────────
        val exportSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(BtnShape)
                .background(Color(0xFF1565C0))          // distinct blue colour
                .clickable(exportSource, ripple(color = Color.White)) {
                    val file = PdfExporter.exportBatchReport(
                        context = context,
                        batches = persistedBatches      // pass Room list directly
                    )
                    Toast.makeText(
                        context,
                        if (file != null) "Report saved: ${file.name}"
                        else "Export failed — no batches yet",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "📄 Export Batch Report PDF",
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
        }
        // ─────────────────────────────────────────────────────────────────

        Spacer(Modifier.height(20.dp))
        Text(
            "BATCH HISTORY",
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextLow,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(8.dp))

        // ── Totals computed from persisted Room data ───────────────────────
        val totalPoles  = persistedBatches.sumOf { it.poles }
        val totalStrips = persistedBatches.sumOf { it.strips }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TotalChip("Poles: $totalPoles total")
            TotalChip("Strips: $totalStrips total")
        }
        Spacer(Modifier.height(10.dp))

        if (persistedBatches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceColor)
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No batches yet. Log your first batch above!",
                    fontSize = 13.sp,
                    color = TextLow,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            persistedBatches.forEach { entity ->
                // ── Map BatchEntity → BatchEntry for the card composable ──
                BatchCard(
                    BatchEntity(
                        product = entity.product,
                        poles   = entity.poles,
                        strips  = entity.strips,
                        date    = entity.date
                    )
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// 3. PRICER SCREEN  ── fully wired to PricerViewModel (NFR13 fix)
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun PricerScreen(
    navController: NavHostController,
    vm: PricerViewModel = viewModel()            // ← ViewModel injected
) {
    // ── All state now lives in the ViewModel ──────────────────────────────
    val materialCost by vm.materialCost.collectAsState()
    val hoursWorked  by vm.hoursWorked.collectAsState()
    val overhead     by vm.overhead.collectAsState()
    val result       by vm.result.collectAsState()

    ScreenScaffold("Price Suggester", "Rs", navController) {

        Text(
            "ENTER YOUR COSTS",
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextLow,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, CardShape),
            shape    = CardShape,
            colors   = CardDefaults.cardColors(containerColor = SurfaceColor)
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ── Inputs delegate to VM setters ─────────────────────────
                TrackerField(
                    label         = "Material Cost (Rs per unit)",
                    placeholder   = "e.g. 350",
                    value         = materialCost,
                    keyboard      = KeyboardType.Number,
                    onValueChange = { vm.materialCost.value = it }
                )
                TrackerField(
                    label         = "Hours Worked",
                    placeholder   = "e.g. 6",
                    value         = hoursWorked,
                    keyboard      = KeyboardType.Number,
                    onValueChange = { vm.hoursWorked.value = it }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentXL)
                        .padding(10.dp)
                ) {
                    Text(
                        "Labour rate assumed Rs 80/hr (editable in Settings)",
                        fontSize = 11.sp,
                        color = Accent,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Overhead %",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextMed
                        )
                        Text(
                            "$overhead%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Primary
                        )
                    }
                    Slider(
                        value         = overhead.toFloatOrNull() ?: 20f,
                        onValueChange = { vm.overhead.value = it.toInt().toString() },
                        valueRange    = 0f..60f,
                        steps         = 59,
                        colors        = SliderDefaults.colors(
                            thumbColor       = Primary,
                            activeTrackColor = Primary
                        )
                    )
                }

                val calcSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(BtnShape)
                        .background(Primary)
                        .clickable(calcSource, ripple(color = Color.White)) {
                            vm.calculate()     // ← single call — all logic in VM
                        }
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Calculate Price",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        result?.let { r ->
            Spacer(Modifier.height(20.dp))
            Text(
                "PRICE BREAKDOWN",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextLow,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(10.dp))
            PriceResultCard(r)
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// 4. MARKETPLACE SCREEN  ── fully wired to MarketplaceViewModel (NFR13 fix)
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun MarketplaceScreen(
    navController: NavHostController,
    vm: MarketplaceViewModel = viewModel()
) {
    val listing by vm.listing.collectAsState()
    val categories = listOf("Furniture", "Baskets", "Lighting", "Decor", "Bamboo")

    // ── Photo Picker State ─────────────────────────────────────────────────
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showPreview by remember { mutableStateOf(false) }
    var imgScale by remember { mutableStateOf(1f) }
    var imgOffsetX by remember { mutableStateOf(0f) }
    var imgOffsetY by remember { mutableStateOf(0f) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            imgScale = 1f
            imgOffsetX = 0f
            imgOffsetY = 0f
        }
    }

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        imgScale = (imgScale * zoomChange).coerceIn(0.5f, 5f)
        imgOffsetX += panChange.x
        imgOffsetY += panChange.y
    }

    ScreenScaffold("Marketplace", "🛒", navController) {

        Text(
            "CREATE LISTING",
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextLow,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, CardShape),
            shape = CardShape,
            colors = CardDefaults.cardColors(containerColor = SurfaceColor)
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // ── Photo Box ──────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0EBE0))
                        .border(
                            width = 2.dp,
                            color = if (selectedImageUri != null) Primary else BorderColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Product Photo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = imgScale,
                                    scaleY = imgScale,
                                    translationX = imgOffsetX,
                                    translationY = imgOffsetY
                                )
                                .transformable(state = transformState)
                        )
                        // ── "Tap to change" hint overlay ───────────────────
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xAA000000))
                                .clickable { imagePickerLauncher.launch("image/*") }
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Tap to change photo",
                                color = TextWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📸", fontSize = 32.sp)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Tap to add photo",
                                fontSize = 12.sp,
                                color = TextLow,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // ── Product Title ──────────────────────────────────────────
                TrackerField(
                    label = "Product Title",
                    placeholder = "e.g. Handwoven Rattan Chair",
                    value = listing.title,
                    keyboard = KeyboardType.Text,
                    onValueChange = { vm.updateTitle(it) }
                )

                // ── Category Chips ─────────────────────────────────────────
                Column {
                    Text(
                        "Category",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextMed
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            val isOn = cat == listing.category
                            val csrc = remember { MutableInteractionSource() }
                            Box(
                                modifier = Modifier
                                    .clip(ChipShape)
                                    .background(if (isOn) Primary else SurfaceColor)
                                    .border(
                                        1.5.dp,
                                        if (isOn) Color.Transparent else BorderColor,
                                        ChipShape
                                    )
                                    .clickable(csrc, ripple()) { vm.updateCategory(cat) }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    cat,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isOn) TextWhite else TextMed
                                )
                            }
                        }
                    }

                    // ── Description ────────────────────────────────────────────
                    Column {
                        Text(
                            "Description",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextMed
                        )
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = listing.description,
                            onValueChange = { vm.updateDescription(it) },
                            placeholder = {
                                Text(
                                    "Describe your product...",
                                    fontSize = 13.sp,
                                    color = TextLow
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = TextHigh,
                                unfocusedTextColor = TextHigh
                            )
                        )
                    }

                    // ── Price ──────────────────────────────────────────────────
                    TrackerField(
                        label = "Price (Rs)",
                        placeholder = "e.g. 1499",
                        value = listing.price,
                        keyboard = KeyboardType.Number,
                        onValueChange = { vm.updatePrice(it) }
                    )

                    // ── Preview Button ─────────────────────────────────────────
                    val previewSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(BtnShape)
                            .background(Accent)
                            .clickable(previewSource, ripple(color = Color.White)) {
                                showPreview = true
                            }
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Preview as Buyer",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // ── Buyer Preview Card ─────────────────────────────────────────────
            if (showPreview) {
                Spacer(Modifier.height(20.dp))
                Text(
                    "BUYER PREVIEW",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextLow,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(10.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, CardShape),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                ) {
                    Column {
                        // ── Product Image Area ─────────────────────────────────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color(0xFFF0EBE0)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Product Image",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(
                                    text = when (listing.category.lowercase()) {
                                        "furniture" -> "🪑"
                                        "baskets" -> "🧺"
                                        "lighting" -> "💡"
                                        "decor" -> "🏺"
                                        "bamboo" -> "🎋"
                                        else -> "🛒"
                                    },
                                    fontSize = 56.sp
                                )
                            }

                            // ── NEW LISTING badge ──────────────────────────────
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Primary)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "NEW LISTING",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextWhite
                                )
                            }
                        }

                        // ── Product Details ────────────────────────────────────
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                listing.category.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Primary,
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                listing.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextHigh
                            )
                            if (listing.description.isNotBlank()) {
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    listing.description,
                                    fontSize = 12.sp,
                                    color = TextMed
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        "Rs ${listing.price.ifBlank { "0" }}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Accent
                                    )
                                    Text(
                                        "per unit",
                                        fontSize = 10.sp,
                                        color = TextLow
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(BtnShape)
                                        .background(Accent)
                                        .padding(horizontal = 16.dp, vertical = 9.dp)
                                ) {
                                    Text(
                                        "Add to Cart",
                                        color = TextWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryXL)
                        .padding(14.dp)
                ) {
                    Text(
                        "This is how buyers will see your listing on the marketplace.",
                        fontSize = 12.sp,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    @Composable
    fun ScreenScaffold(
        x0: String,
        x1: String,
        x2: NavHostController,
        content: @Composable () -> Unit
    ) {
        TODO("Not yet implemented")
    }

// ════════════════════════════════════════════════════════════════════════════
// HELPER COMPOSABLES — Blueprint
// ════════════════════════════════════════════════════════════════════════════

    @Composable
    fun DimBadge(text: String) {
        Box(
            modifier = Modifier
                .clip(ChipShape)
                .background(PrimaryXL)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(text, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
        }
    }

    @Composable
    fun StepCard(stepNum: Int, text: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceColor)
                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stepNum.toString(),
                    color = TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Text(
                text,
                fontSize = 13.sp,
                color = TextMed,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }

// ════════════════════════════════════════════════════════════════════════════
// HELPER COMPOSABLES — Tracker
// ════════════════════════════════════════════════════════════════════════════

    @Composable
    fun TrackerField(
        label: String,
        placeholder: String,
        value: String,
        keyboard: KeyboardType,
        onValueChange: (String) -> Unit
    ) {
        Column {
            Text(
                label,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextMed,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, fontSize = 13.sp, color = TextLow) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = keyboard),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = TextHigh,
                    unfocusedTextColor = TextHigh
                ),
                singleLine = true
            )
        }
    }

    @Composable
    fun TotalChip(text: String) {
        Box(
            modifier = Modifier
                .clip(ChipShape)
                .background(PrimaryXL)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(text, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
        }
    }

    @Composable
    fun BatchCard(batch: BatchEntity) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(SurfaceColor)
                .border(1.dp, BorderColor, CardShape)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    batch.product,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHigh
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    batch.date,
                    fontSize = 11.sp,
                    color = TextLow,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MatBadge("Poles: ${batch.poles}")
                MatBadge("Strips: ${batch.strips}")
            }
        }
    }

    @Composable
    fun MatBadge(text: String) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5))
                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = TextMed)
        }
    }

// ════════════════════════════════════════════════════════════════════════════
// HELPER COMPOSABLES — Pricer
// ════════════════════════════════════════════════════════════════════════════

    @Composable
    fun PriceResultCard(r: PriceResult) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, CardShape),
            shape = CardShape,
            colors = CardDefaults.cardColors(containerColor = SurfaceColor)
        ) {
            Column(Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.verticalGradient(listOf(Color(0xFF1B5E20), Primary)))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Suggested Selling Price",
                            color = Color(0xBFFFFFFF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Rs ${r.suggestedPrice.toInt()}",
                            color = TextWhite,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            "per unit",
                            color = Color(0xBFFFFFFF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                PriceRow("Material cost", "Rs ${r.material.toInt()}", isTotal = false)
                PriceRow(
                    "Labour (${(r.labour / 80).toInt()} hrs x Rs 80)",
                    "Rs ${r.labour.toInt()}",
                    isTotal = false
                )
                PriceRow(
                    "Overhead (${r.overheadPct.toInt()}%)",
                    "Rs ${(r.base * r.overheadPct / 100).toInt()}",
                    isTotal = false
                )
                HorizontalDivider(color = BorderColor, modifier = Modifier.padding(vertical = 8.dp))
                PriceRow("Your profit margin", "Rs ${r.margin.toInt()}", isTotal = true)
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryXL)
                        .padding(10.dp)
                ) {
                    Text(
                        "Formula: (Material + Labour) x (1 + Overhead%)",
                        fontSize = 11.sp,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    @Composable
    fun PriceRow(label: String, value: String, isTotal: Boolean) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                fontSize = if (isTotal) 14.sp else 13.sp,
                fontWeight = if (isTotal) FontWeight.Black else FontWeight.SemiBold,
                color = if (isTotal) TextHigh else TextMed
            )
            Text(
                value,
                fontSize = if (isTotal) 16.sp else 13.sp,
                fontWeight = FontWeight.Black,
                color = if (isTotal) Primary else TextMed
            )
        }
    }

// ════════════════════════════════════════════════════════════════════════════
// HELPER COMPOSABLES — Marketplace
// ════════════════════════════════════════════════════════════════════════════

    @Composable
    fun BuyerPreviewCard(
        title: String,
        description: String,
        price: String,
        category: String,
        imageUri: Uri? = null          // ← NEW PARAMETER
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, CardShape),
            shape = CardShape,
            colors = CardDefaults.cardColors(containerColor = SurfaceColor)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(Color(0xFFF0EBE0)),
                    contentAlignment = Alignment.Center
                ) {
                    // ── Show real image if selected, else emoji fallback ────────
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Product Image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            when (category) {
                                "Furniture" -> "🪑"
                                "Baskets" -> "🧺"
                                "Lighting" -> "💡"
                                else -> "🎋"
                            },
                            fontSize = 52.sp
                        )
                    }

                    // ── NEW LISTING badge — always visible ─────────────────────
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(ChipShape)
                            .background(Primary)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "NEW LISTING",
                            color = TextWhite,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Column(Modifier.padding(14.dp)) {
                    Text(
                        category.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextLow,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(title, fontSize = 17.sp, fontWeight = FontWeight.Black, color = TextHigh)
                    if (description.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(description, fontSize = 13.sp, color = TextMed, lineHeight = 18.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Rs ${price.ifBlank { "--" }}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Primary
                            )
                            Text(
                                "per unit",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextLow
                            )
                        }
                        val buySource = remember { MutableInteractionSource() }
                        Box(
                            modifier = Modifier
                                .clip(BtnShape)
                                .background(Accent)
                                .clickable(buySource, ripple(color = Color.White)) { }
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                "Add to Cart",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

    }
}
// ════════════════════════════════════════════════════════════════════════════
// HELPER COMPOSABLES — Blueprint
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun DimBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(PrimaryXL)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
    }
}

@Composable
fun StepCard(stepNum: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceColor)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stepNum.toString(),
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
        }
        Text(
            text,
            fontSize = 13.sp,
            color = TextMed,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// HELPER COMPOSABLES — Tracker
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun TrackerField(
    label: String,
    placeholder: String,
    value: String,
    keyboard: KeyboardType,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextMed,
            letterSpacing = 0.5.sp
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value           = value,
            onValueChange   = onValueChange,
            placeholder     = { Text(placeholder, fontSize = 13.sp, color = TextLow) },
            modifier        = Modifier.fillMaxWidth(),
            shape           = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboard),
            colors          = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = Primary,
                unfocusedBorderColor = BorderColor,
                focusedTextColor     = TextHigh,
                unfocusedTextColor   = TextHigh
            ),
            singleLine = true
        )
    }
}

@Composable
fun TotalChip(text: String) {
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(PrimaryXL)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
    }
}

@Composable
fun BatchCard(batch: BatchEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(SurfaceColor)
            .border(1.dp, BorderColor, CardShape)
            .padding(14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                batch.product,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextHigh
            )
            Spacer(Modifier.height(4.dp))
            Text(
                batch.date,
                fontSize = 11.sp,
                color = TextLow,
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MatBadge("Poles: ${batch.poles}")
            MatBadge("Strips: ${batch.strips}")
        }
    }
}

@Composable
fun MatBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = TextMed)
    }
}

// ════════════════════════════════════════════════════════════════════════════
// HELPER COMPOSABLES — Pricer
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun PriceResultCard(r: PriceResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, CardShape),
        shape    = CardShape,
        colors   = CardDefaults.cardColors(containerColor = SurfaceColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFF1B5E20), Primary)))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Suggested Selling Price",
                        color = Color(0xBFFFFFFF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Rs ${r.suggestedPrice.toInt()}",
                        color = TextWhite,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "per unit",
                        color = Color(0xBFFFFFFF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            PriceRow("Material cost", "Rs ${r.material.toInt()}", isTotal = false)
            PriceRow(
                "Labour (${(r.labour / 80).toInt()} hrs x Rs 80)",
                "Rs ${r.labour.toInt()}",
                isTotal = false
            )
            PriceRow(
                "Overhead (${r.overheadPct.toInt()}%)",
                "Rs ${(r.base * r.overheadPct / 100).toInt()}",
                isTotal = false
            )
            HorizontalDivider(color = BorderColor, modifier = Modifier.padding(vertical = 8.dp))
            PriceRow("Your profit margin", "Rs ${r.margin.toInt()}", isTotal = true)
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryXL)
                    .padding(10.dp)
            ) {
                Text(
                    "Formula: (Material + Labour) x (1 + Overhead%)",
                    fontSize = 11.sp,
                    color = Primary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PriceRow(label: String, value: String, isTotal: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize   = if (isTotal) 14.sp else 13.sp,
            fontWeight = if (isTotal) FontWeight.Black else FontWeight.SemiBold,
            color      = if (isTotal) TextHigh else TextMed
        )
        Text(
            value,
            fontSize   = if (isTotal) 16.sp else 13.sp,
            fontWeight = FontWeight.Black,
            color      = if (isTotal) Primary else TextMed
        )
    }
}
