package com.hastashilpa.app.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// Reuses the same design tokens from your app
private val Primary   = Color(0xFF2E7D32)
private val PrimaryXL = Color(0xFFE8F5E9)
private val Accent    = Color(0xFFE65100)
private val AccentXL  = Color(0xFFFFF3E0)
private val TextHigh  = Color(0xFF1A1A1A)
private val TextMed   = Color(0xFF424242)
private val TextLow   = Color(0xFF9E9E9E)
private val TextWhite = Color(0xFFFFFFFF)
private val CardShape = RoundedCornerShape(20.dp)
private val BorderColor = Color(0xFFEEEEEE)
private val SurfaceColor = Color(0xFFFAFAFA)

// ── Simulated summary data ─────────────────────────────────────────────────
private data class WeeklyStat(val label: String, val value: Int, val unit: String, val emoji: String)
private data class EarningRow(val product: String, val qty: Int, val priceEach: Int)

@Composable
fun ProgressDashboardScreen(navController: NavHostController) {

    // These would come from Room in a full implementation;
    // for now they mirror the seeded Tracker data so UI is always populated.
    val weeklyStats = listOf(
        WeeklyStat("Batches Done",    4,   "batches", "📦"),
        WeeklyStat("Bamboo Poles",   42,   "poles",   "🎋"),
        WeeklyStat("Cane Strips",   145,   "strips",  "🪣"),
        WeeklyStat("Est. Earnings", 9876, "Rs",       "💰")
    )
    val earningRows = listOf(
        EarningRow("Rattan Bloom Chair",    3, 1499),
        EarningRow("Cane Storage Trunk",    5,  699),
        EarningRow("Woven Bamboo Lamp",     2, 2891),
        EarningRow("Bamboo Flower Basket",  6,  499)
    )
    val totalEarnings = earningRows.sumOf { it.qty * it.priceEach }

    ScreenScaffold("Progress Dashboard", "📊", navController) {

        // ── Hero earnings card ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, CardShape)
                .clip(CardShape)
                .background(
                    Brush.linearGradient(listOf(Color(0xFF1B5E20), Primary))
                )
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()) {
                Text(
                    "This Month's Estimated Earnings",
                    color      = Color(0xBFFFFFFF),
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "₹$totalEarnings",
                    color      = TextWhite,
                    fontSize   = 38.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0x33FFFFFF))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        "↑ 14% vs last month",
                        color      = Color(0xCCFFFFFF),
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── 4-stat grid ───────────────────────────────────────────────────
        Text(
            "WEEKLY SNAPSHOT",
            fontSize      = 10.sp,
            fontWeight    = FontWeight.ExtraBold,
            color         = TextLow,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(10.dp))
        val rows = weeklyStats.chunked(2)
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { stat ->
                    StatCard(stat, Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(8.dp))

        // ── Earnings breakdown table ───────────────────────────────────────
        Text(
            "PRODUCT EARNINGS BREAKDOWN",
            fontSize      = 10.sp,
            fontWeight    = FontWeight.ExtraBold,
            color         = TextLow,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth().shadow(4.dp, CardShape),
            shape    = CardShape,
            colors   = CardDefaults.cardColors(containerColor = SurfaceColor)
        ) {
            Column(Modifier.padding(16.dp)) {
                // Header row
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Text("Product",  fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                        color = TextLow, modifier = Modifier.weight(1f))
                    Text("Qty",      fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                        color = TextLow, textAlign = TextAlign.Center,
                        modifier = Modifier.width(36.dp))
                    Text("Total",    fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                        color = TextLow, textAlign = TextAlign.End,
                        modifier = Modifier.width(72.dp))
                }
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = BorderColor)

                earningRows.forEach { row ->
                    Spacer(Modifier.height(10.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        Text(
                            row.product,
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextMed,
                            modifier   = Modifier.weight(1f)
                        )
                        Text(
                            "×${row.qty}",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = TextLow,
                            textAlign  = TextAlign.Center,
                            modifier   = Modifier.width(36.dp)
                        )
                        Text(
                            "₹${row.qty * row.priceEach}",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Black,
                            color      = Primary,
                            textAlign  = TextAlign.End,
                            modifier   = Modifier.width(72.dp)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Text("Grand Total",
                        fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextHigh)
                    Text("₹$totalEarnings",
                        fontSize = 16.sp, fontWeight = FontWeight.Black, color = Primary)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Material efficiency tip ────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(AccentXL)
                .border(1.dp, Color(0x26E65100), CardShape)
                .padding(16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("💡", fontSize = 26.sp)
            Column {
                Text("Efficiency Tip",
                    fontSize = 12.sp, fontWeight = FontWeight.Black, color = Accent)
                Spacer(Modifier.height(2.dp))
                Text(
                    "You used 42 poles this week. Buying in bulk (50+) reduces cost by ~12%.",
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextMed,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// ── Stat card composable ───────────────────────────────────────────────────
@Composable
private fun StatCard(stat: WeeklyStat, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.shadow(4.dp, CardShape),
        shape    = CardShape,
        colors   = CardDefaults.cardColors(containerColor = SurfaceColor)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryXL),
                    contentAlignment = Alignment.Center
                ) { Text(stat.emoji, fontSize = 15.sp) }
                Text(
                    stat.label,
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = TextLow,
                    lineHeight = 13.sp
                )
            }
            Text(
                if (stat.unit == "Rs") "₹${stat.value}" else "${stat.value}",
                fontSize   = 24.sp,
                fontWeight = FontWeight.Black,
                color      = TextHigh
            )
            Text(
                stat.unit,
                fontSize   = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextLow
            )
        }
    }
}