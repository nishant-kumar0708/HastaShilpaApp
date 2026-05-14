package com.hastashilpa.app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hastashilpa.app.data.UserEntity
import com.hastashilpa.app.navigation.AppNavGraph
import com.hastashilpa.app.navigation.Screen
import com.hastashilpa.app.util.NetworkUtils
import com.hastashilpa.app.util.ProductImageUrls
import com.hastashilpa.app.util.UnsplashImage
import com.hastashilpa.app.viewmodel.AuthViewModel

// ══════════════════════════════════════════════════════
// DESIGN TOKENS
// ══════════════════════════════════════════════════════
internal val Primary      = Color(0xFF2E7D32)
internal val PrimaryXL    = Color(0xFFE8F5E9)
internal val Accent       = Color(0xFFE65100)
internal val AccentXL     = Color(0xFFFFF3E0)
internal val BgColor      = Color(0xFFF5F5F5)
internal val SurfaceColor = Color(0xFFFAFAFA)
internal val Surface2     = Color(0xFFFFFFFF)
internal val BorderColor  = Color(0xFFEEEEEE)
internal val TextHigh     = Color(0xFF1A1A1A)
internal val TextMed      = Color(0xFF424242)
internal val TextLow      = Color(0xFF9E9E9E)
internal val TextWhite    = Color(0xFFFFFFFF)

internal val CardShape = RoundedCornerShape(20.dp)
internal val ChipShape = RoundedCornerShape(50.dp)
internal val BtnShape  = RoundedCornerShape(50.dp)
internal val SmShape   = RoundedCornerShape(10.dp)
internal val ImgShape  = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

// ══════════════════════════════════════════════════════
// DATA MODELS
// ══════════════════════════════════════════════════════
data class Category(val icon: String, val label: String)

data class Product(
    val category      : String,
    val name          : String,
    val artisan       : String,
    val location      : String,
    val rating        : Double,
    val reviewCount   : Int,
    val price         : Int,
    val originalPrice : Int?,
    val badgeText     : String,
    val badgeType     : String,
    val isEco         : Boolean,
    val tags          : List<String>,
    val lookingCount  : Int = 42
)

// ══════════════════════════════════════════════════════
// MAIN ACTIVITY
// ══════════════════════════════════════════════════════
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.parseColor("#1B5E20")
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            MaterialTheme {
                AppNavGraph(navController = navController)
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// HOME SCREEN
// ══════════════════════════════════════════════════════
@Composable
fun HomeScreen(navController: NavHostController) {
    val authVm: AuthViewModel = viewModel()
    val categories = listOf(
        Category("🌿", "All"),
        Category("🧺", "Baskets"),
        Category("🪑", "Furniture"),
        Category("🪔", "Decor"),
        Category("💡", "Lighting"),
        Category("🎋", "Bamboo")
    )

    val products = listOf(
        Product("Furniture", "Rattan Bloom Chair",        "Kaveri Nair",     "Wayanad",     4.9, 124, 1499, 1899, "Bestseller",  "gold", true,  listOf("hm","ec"), 64),
        Product("Baskets",   "Cane Storage Trunk",        "Raju Gowda",      "Coorg",       4.6,  87,  699, null, "New Arrival", "grn",  false, listOf("hm"),       89),
        Product("Lighting",  "Woven Bamboo Lamp",         "Meera Shetty",    "Sirsi",       4.8, 203, 2891, 3499, "Trending",   "org",  true,  listOf("hm","ec"), 78),
        Product("Baskets",   "Bamboo Flower Basket",      "Lakshmi Bai",     "Agumbe",      4.5,  61,  499, null, "Eco Pick",   "gold", false, listOf("ec"),       34),
        Product("Furniture", "Cane Peacock Chair",        "Suresh Kumar",    "Udupi",       4.7,  98, 3499, 4200, "Trending",   "grn",  true,  listOf("hm","ec"), 51),
        Product("Decor",     "Bamboo Wall Art Panel",     "Priya Hegde",     "Dharwad",     4.4,  45,  899, null, "New Arrival","grn",  true,  listOf("hm","ec"), 27),
        Product("Baskets",   "Cane Picnic Hamper",        "Anita Rao",       "Mysuru",      4.6,  73, 1299, 1599, "Bestseller", "gold", false, listOf("hm"),       43),
        Product("Lighting",  "Rattan Pendant Light",      "Deepa Nair",      "Kozhikode",   4.9,  88, 1899, 2299, "Bestseller", "gold", true,  listOf("hm","ec"), 62),
        Product("Decor",     "Woven Cane Tray Set",       "Geetha Bhat",     "Mangaluru",   4.3,  39,  599, null, "Eco Pick",   "grn",  true,  listOf("ec"),       19),
        Product("Furniture", "Bamboo Lounge Stool",       "Ramesh Nayak",    "Shimoga",     4.7, 112, 2199, 2799, "Trending",   "org",  true,  listOf("hm","ec"), 56),
        Product("Baskets",   "Cane Laundry Basket",       "Suma Kamath",     "Udupi",       4.5,  66,  849, null, "New Arrival","grn",  false, listOf("hm"),       38),
        Product("Lighting",  "Bamboo Table Lamp",         "Kavitha Shenoy",  "Sirsi",       4.6,  54, 1599, 1999, "Trending",   "org",  true,  listOf("hm","ec"), 45),
        Product("Decor",     "Rattan Fruit Bowl",         "Leela Naik",      "Belgaum",     4.4,  48,  449, null, "Eco Pick",   "grn",  true,  listOf("ec"),       22),
        Product("Furniture", "Cane Bar Stool",            "Harish Gowda",    "Hassan",      4.8,  91, 2799, 3299, "Bestseller", "gold", false, listOf("hm"),       67),
        Product("Baskets",   "Bamboo Bread Basket",       "Shanta Reddy",    "Chitradurga", 4.3,  35,  399, null, "New Arrival","grn",  false, listOf("hm","ec"), 15),
        Product("Decor",     "Woven Bamboo Mirror Frame", "Nandini Kulkarni","Hubli",       4.7,  79, 1199, 1499, "Trending",   "org",  true,  listOf("hm","ec"), 41)
    )

    var selectedCategory by remember { mutableStateOf("All") }
    var activeNavIndex   by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets(0))
    ) {
        StatusBar()

        val context   = LocalContext.current
        val isOffline = !NetworkUtils.isOnline(context)
        if (isOffline) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE65100))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    "📡 You are offline — showing cached content",
                    color      = Color.White,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Column(Modifier.weight(1f)) {
            AppHeader(navController = navController, authVm = authVm)
            ScrollBody(
                categories       = categories,
                products         = products,
                selectedCategory = selectedCategory,
                onCategorySelect = { selectedCategory = it },
                onBlueprintClick = { navController.navigate(Screen.Blueprint.route) },
                onDashboardClick = { navController.navigate(Screen.Dashboard.route) }
            )
        }

        BottomNav(
            activeIndex = activeNavIndex,
            onNavSelect = {
                activeNavIndex = it
                when (it) {
                    1 -> navController.navigate(Screen.Blueprint.route)
                    2 -> navController.navigate(Screen.GenAi.route)
                    3 -> navController.navigate(Screen.Pricer.route)
                    4 -> navController.navigate(Screen.Marketplace.route)
                }
            }
        )
    }
}

// ══════════════════════════════════════════════════════
// PHONE SHELL
// ══════════════════════════════════════════════════════
@Composable
fun PhoneShell(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .width(393.dp)
            .wrapContentHeight()
            .shadow(40.dp, RoundedCornerShape(44.dp))
            .clip(RoundedCornerShape(44.dp))
            .background(BgColor)
    ) { content() }
}

// ══════════════════════════════════════════════════════
// STATUS BAR
// ══════════════════════════════════════════════════════
@Composable
fun StatusBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B5E20))
            .windowInsetsTopHeight(WindowInsets.statusBars)
    )
}

// ══════════════════════════════════════════════════════
// HEADER
// ══════════════════════════════════════════════════════
@Composable
fun AppHeader(navController: NavHostController, authVm: AuthViewModel) {
    var visible by remember { mutableStateOf(false) }
    val offsetY by animateFloatAsState(
        targetValue   = if (visible) 0f else 20f,
        animationSpec = tween(380),
        label         = "headerSlide"
    )
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0xFF1B5E20), Primary)))
            .graphicsLayer { translationY = offsetY }
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp)
    ) {
        Canvas(Modifier.matchParentSize()) {
            var x = 0f
            while (x < size.width) {
                drawLine(Color(0x04FFFFFF), Offset(x, 0f), Offset(x, size.height), 1f)
                x += 21f
            }
        }
        Column {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Artisan Marketplace",
                        color       = Color(0x80FFFFFF),
                        fontSize    = 9.sp,
                        fontWeight  = FontWeight.ExtraBold,
                        letterSpacing = 3.sp
                    )
                    Row {
                        Text(
                            "Hasta\u2011",
                            color       = TextWhite,
                            fontSize    = 26.sp,
                            fontWeight  = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            "Shilpa",
                            color       = Color(0xFFFFB300),
                            fontSize    = 26.sp,
                            fontWeight  = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                    }
                }
                AvatarButton(
                    currentUser = authVm.currentUser.collectAsState().value,
                    onLogout    = {
                        authVm.logout()
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "AI-Powered · Design · Sell · Learn",
                color       = Color(0x59FFFFFF),
                fontSize    = 9.sp,
                fontWeight  = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                SearchBar(Modifier.weight(1f))
                FilterButton()
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// AVATAR BUTTON
// ══════════════════════════════════════════════════════
@Composable
fun AvatarButton(
    onLogout    : () -> Unit = {},
    currentUser : UserEntity? = null
) {
    var hasNotif    by remember { mutableStateOf(true) }
    var showMenu    by remember { mutableStateOf(false) }
    var showProfile by remember { mutableStateOf(false) }

    Box {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFFFFB300), Color(0xFFFF8F00))))
                .border(2.dp, Color(0x33FFFFFF), CircleShape)
                .clickable {
                    hasNotif = false
                    showMenu = true
                },
            contentAlignment = Alignment.Center
        ) {
            Text("🧑", fontSize = 16.sp)
            if (hasNotif) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 1.dp, y = (-1).dp)
                        .clip(CircleShape)
                        .background(Accent)
                        .border(2.dp, Primary, CircleShape)
                )
            }
        }

        DropdownMenu(
            expanded         = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text    = { Text("My Profile", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                onClick = {
                    showMenu    = false
                    showProfile = true
                }
            )
            DropdownMenuItem(
                text    = { Text("Logout", fontSize = 13.sp, color = Color(0xFFB91C1C)) },
                onClick = { showMenu = false; onLogout() }
            )
        }
    }

    if (showProfile) {
        Dialog(onDismissRequest = { showProfile = false }) {
            Card(
                shape  = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8EF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFFB300), Color(0xFFFF8F00))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🧑", fontSize = 32.sp)
                    }

                    Spacer(Modifier.height(12.dp))

                    if (currentUser == null || currentUser.loginMethod == "GUEST") {
                        Text(
                            "Guest Artisan",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color(0xFF1A1A1A)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Browsing as guest",
                            fontSize = 13.sp,
                            color    = Color(0xFF9E9E9E)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Register to save your designs,\ntrack materials and sell products.",
                            fontSize   = 12.sp,
                            color      = Color(0xFF57534E),
                            textAlign  = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    } else {
                        Text(
                            currentUser.name.ifBlank { "Artisan" },
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color(0xFF1A1A1A)
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                when (currentUser.loginMethod) {
                                    "EMAIL" -> "✉ Email Account"
                                    "PHONE" -> "📱 Phone Account"
                                    else    -> "🔑 Registered"
                                },
                                fontSize   = 11.sp,
                                color      = Color(0xFF2E7D32),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                        ProfileRow("✉", "Email",    currentUser.email.ifBlank { "—" })
                        if (currentUser.phone.isNotBlank())
                            ProfileRow("📱", "Phone", "+91 ${currentUser.phone}")
                        ProfileRow("🎋", "Craft",    currentUser.craft.ifBlank { "Bamboo & Cane" })
                        if (currentUser.location.isNotBlank())
                            ProfileRow("📍", "Location", currentUser.location)
                        ProfileRow("🆔", "Member ID", "#${currentUser.id.toString().padStart(4, '0')}")
                    }

                    Spacer(Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2E7D32))
                            .clickable { showProfile = false }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// PROFILE ROW HELPER
// ══════════════════════════════════════════════════════
@Composable
private fun ProfileRow(icon: String, label: String, value: String) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 16.sp, modifier = Modifier.width(28.dp))
        Column {
            Text(
                label,
                fontSize      = 10.sp,
                color         = Color(0xFF9E9E9E),
                fontWeight    = FontWeight.ExtraBold,
                letterSpacing = 0.8.sp
            )
            Text(
                value,
                fontSize   = 13.sp,
                color      = Color(0xFF1A1A1A),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFEEEEEE))
    )
}

// ══════════════════════════════════════════════════════
// SEARCH BAR
// ══════════════════════════════════════════════════════
@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .clip(BtnShape)
            .background(Color(0x1FFFFFFF))
            .border(1.5.dp, Color(0x26FFFFFF), BtnShape)
            .clickable(interactionSource, ripple()) { }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("🔍", fontSize = 14.sp)
        Text(
            "Search designs, products…",
            color      = Color(0x73FFFFFF),
            fontSize   = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            modifier   = Modifier.weight(1f)
        )
        Text("🎙️", fontSize = 14.sp)
    }
}

// ══════════════════════════════════════════════════════
// FILTER BUTTON
// ══════════════════════════════════════════════════════
@Composable
fun FilterButton() {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(SmShape)
            .background(Color(0x1FFFFFFF))
            .border(1.5.dp, Color(0x26FFFFFF), SmShape)
            .clickable(interactionSource, ripple()) { },
        contentAlignment = Alignment.Center
    ) {
        Text("⚙️", fontSize = 16.sp)
    }
}

// ══════════════════════════════════════════════════════
// SCROLL BODY
// ══════════════════════════════════════════════════════
@Composable
fun ScrollBody(
    categories       : List<Category>,
    products         : List<Product>,
    selectedCategory : String,
    onCategorySelect : (String) -> Unit,
    onBlueprintClick : () -> Unit,
    onDashboardClick : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        SectionHeader("Categories", "See all")
        CategoryRow(categories, selectedCategory, onCategorySelect)
        Spacer(Modifier.height(16.dp))
        TrendingBanner(onBlueprintClick)
        Spacer(Modifier.height(16.dp))
        SectionHeader("Popular Products", "See all")
        ProductGrid(products)
        Spacer(Modifier.height(16.dp))
        SupportStrip()
        Spacer(Modifier.height(8.dp))
        DashboardShortcut(onDashboardClick)
    }
}

// ══════════════════════════════════════════════════════
// DASHBOARD SHORTCUT
// ══════════════════════════════════════════════════════
@Composable
fun DashboardShortcut(onClick: () -> Unit) {
    val src = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(CardShape)
            .background(PrimaryXL)
            .border(1.dp, Color(0x26FFB300), CardShape)
            .clickable(src, ripple()) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("📊", fontSize = 22.sp)
            Column {
                Text(
                    "Progress Dashboard",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Black,
                    color      = Primary
                )
                Text(
                    "Batches · Materials · Earnings",
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextLow
                )
            }
        }
        Text("→", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Primary)
    }
}

// ══════════════════════════════════════════════════════
// SECTION HEADER
// ══════════════════════════════════════════════════════
@Composable
fun SectionHeader(title: String, linkText: String) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            title,
            fontSize      = 16.sp,
            fontWeight    = FontWeight.ExtraBold,
            color         = TextHigh,
            letterSpacing = (-0.2).sp
        )
        Text(
            linkText,
            fontSize   = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = Primary,
            modifier   = Modifier.clickable(interactionSource, null) { }
        )
    }
}

// ══════════════════════════════════════════════════════
// CATEGORY ROW
// ══════════════════════════════════════════════════════
@Composable
fun CategoryRow(
    categories : List<Category>,
    selected   : String,
    onSelect   : (String) -> Unit
) {
    LazyRow(
        contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { cat ->
            val isOn              = cat.label == selected
            val interactionSource = remember { MutableInteractionSource() }
            Row(
                modifier = Modifier
                    .clip(ChipShape)
                    .background(if (isOn) Primary else Surface2)
                    .border(2.dp, if (isOn) Color.Transparent else BorderColor, ChipShape)
                    .shadow(if (isOn) 4.dp else 1.dp, ChipShape)
                    .clickable(interactionSource, ripple()) { onSelect(cat.label) }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(cat.icon,  fontSize = 15.sp)
                Text(
                    cat.label,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = if (isOn) TextWhite else TextMed
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// TRENDING BANNER
// ══════════════════════════════════════════════════════
@Composable
fun TrendingBanner(onBlueprintClick: () -> Unit) {
    var pressed           by remember { mutableStateOf(false) }
    val scale             by animateFloatAsState(if (pressed) 0.98f else 1f, tween(80), label = "banner")
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .scale(scale)
            .shadow(4.dp, CardShape)
            .clip(CardShape)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1B5E20), Primary),
                    Offset(0f, 0f),
                    Offset(500f, 500f)
                )
            )
            .clickable(interactionSource, ripple(color = Color.White)) { pressed = !pressed }
            .padding(16.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .clip(ChipShape)
                        .background(Color(0x33FFB300))
                        .border(1.dp, Color(0x73FFB300), ChipShape)
                        .padding(horizontal = 9.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("🔥", fontSize = 9.sp)
                    Text(
                        "TRENDING NOW",
                        color         = Color(0xFFFFD54F),
                        fontSize      = 9.sp,
                        fontWeight    = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "Bamboo Laptop\nStands are Hot!",
                    color      = TextWhite,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 20.sp,
                    letterSpacing = (-0.3).sp
                )
                Spacer(Modifier.height(10.dp))
                val ctaSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .clip(BtnShape)
                        .background(Accent)
                        .shadow(10.dp, BtnShape)
                        .clickable(ctaSource, ripple(color = Color.White)) { onBlueprintClick() }
                        .padding(horizontal = 16.dp, vertical = 7.dp)
                ) {
                    Text(
                        "View Blueprint →",
                        color         = TextWhite,
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.Black,
                        letterSpacing = 0.3.sp
                    )
                }
            }
            Text("🎋", fontSize = 52.sp)
        }
    }
}

// ══════════════════════════════════════════════════════
// PRODUCT GRID
// ══════════════════════════════════════════════════════
@Composable
fun ProductGrid(products: List<Product>) {
    Column(
        modifier            = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        products.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { product -> ProductCard(product, Modifier.weight(1f)) }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// SHIMMER PAINTER
// ══════════════════════════════════════════════════════
@Composable
fun rememberShimmerPainter(): androidx.compose.ui.graphics.painter.Painter {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue  = 0.20f,
        targetValue   = 0.50f,
        animationSpec = infiniteRepeatable(
            animation  = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    return remember(alpha) {
        object : androidx.compose.ui.graphics.painter.Painter() {
            override val intrinsicSize = Size.Unspecified
            override fun androidx.compose.ui.graphics.drawscope.DrawScope.onDraw() {
                drawRect(Color(0xFFDDD4C8).copy(alpha = alpha))
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// PRODUCT CARD
// ══════════════════════════════════════════════════════
@Composable
fun ProductCard(product: Product, modifier: Modifier = Modifier) {
    var wished    by remember { mutableStateOf(false) }
    var inCart    by remember { mutableStateOf(false) }
    val wishScale by animateFloatAsState(if (wished) 1.45f else 1f, tween(200), label = "wish")
    val cardSource = remember { MutableInteractionSource() }

    Card(
        modifier  = modifier
            .shadow(8.dp, CardShape)
            .clip(CardShape)
            .clickable(cardSource, ripple(color = Color(0x28FFFFFF))) { },
        shape     = CardShape,
        colors    = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp)
                    .clip(ImgShape)
                    .background(Color(0xFFF0EBE0))
            ) {
                UnsplashImage(
                    query        = ProductImageUrls.getQuery(product.category, product.name),
                    index        = 0,
                    modifier     = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder  = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF0EBE0))
                        )
                    }
                )

                // Bottom gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0x22000000))
                            )
                        )
                )

                // Badge
                ProductBadge(product.badgeText, product.badgeType)

                // "X 👀 looking" chip — uses stable lookingCount to avoid recompose flicker
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(ChipShape)
                        .background(Color(0xE6FFFFFF))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        "${product.lookingCount} 👀 looking",
                        fontSize   = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = TextHigh
                    )
                }

                // Eco tag
                if (product.isEco) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(ChipShape)
                            .background(Color(0xD11B5E20))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "🌿 Eco",
                            color         = Color(0xFFA5D6A7),
                            fontSize      = 7.5.sp,
                            fontWeight    = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Wishlist button
                val wishSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .scale(wishScale)
                        .clip(CircleShape)
                        .background(Color(0xE6FFFFFF))
                        .clickable(wishSource, ripple(bounded = false)) { wished = !wished },
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (wished) "❤️" else "🤍", fontSize = 13.sp)
                }
            }

            // Product info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    product.category.uppercase(),
                    fontSize      = 10.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    color         = TextLow,
                    letterSpacing = 1.2.sp
                )
                Spacer(Modifier.height(4.dp))
                if (product.tags.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (product.tags.contains("hm")) MiniTag("HANDMADE", isHandmade = true)
                        if (product.tags.contains("ec")) MiniTag("ECO",      isHandmade = false)
                    }
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    product.name,
                    fontSize      = 16.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = TextHigh,
                    lineHeight    = 19.sp,
                    letterSpacing = (-0.2).sp,
                    maxLines      = 2,
                    overflow      = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(Primary)
                    )
                    Text(
                        "${product.artisan}, ${product.location}",
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextLow
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(product.rating.toInt()) { Text("⭐", fontSize = 10.sp) }
                    Text(
                        product.rating.toString(),
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Black,
                        color      = TextMed
                    )
                    Text(
                        "(${product.reviewCount})",
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextLow
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "₹${product.price}",
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.Black,
                                color      = Primary
                            )
                            product.originalPrice?.let {
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    "₹$it",
                                    fontSize        = 10.sp,
                                    fontWeight      = FontWeight.SemiBold,
                                    color           = TextLow,
                                    textDecoration  = TextDecoration.LineThrough
                                )
                            }
                        }
                        Text(
                            "per unit",
                            fontSize   = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextLow
                        )
                    }
                    AddButton(inCart) { inCart = true }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// PRODUCT BADGE
// ══════════════════════════════════════════════════════
@Composable
fun ProductBadge(text: String, type: String) {
    val bg    = when (type) { "gold" -> Color(0xFFFFB300); "grn" -> Primary; "org" -> Accent; else -> Primary }
    val color = if (type == "gold") Color(0xFF3E2000) else TextWhite
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(ChipShape)
            .background(bg)
            .padding(horizontal = 9.dp, vertical = 3.dp)
    ) {
        Text(
            text.uppercase(),
            color         = color,
            fontSize      = 8.sp,
            fontWeight    = FontWeight.Black,
            letterSpacing = 0.8.sp
        )
    }
}

// ══════════════════════════════════════════════════════
// MINI TAG
// ══════════════════════════════════════════════════════
@Composable
fun MiniTag(text: String, isHandmade: Boolean) {
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(if (isHandmade) PrimaryXL else AccentXL)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text,
            color         = if (isHandmade) Primary else Accent,
            fontSize      = 7.5.sp,
            fontWeight    = FontWeight.Black,
            letterSpacing = 0.4.sp
        )
    }
}

// ══════════════════════════════════════════════════════
// ADD BUTTON
// ══════════════════════════════════════════════════════
@Composable
fun AddButton(inCart: Boolean, onClick: () -> Unit) {
    var pressed           by remember { mutableStateOf(false) }
    val scale             by animateFloatAsState(if (pressed) 0.9f else 1f, tween(80), label = "addBtn")
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(30.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(if (inCart) Primary else Accent)
            .shadow(10.dp, CircleShape)
            .clickable(interactionSource, ripple(bounded = false, color = Color.White)) {
                pressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = if (inCart) "✓" else "+",
            color      = TextWhite,
            fontSize   = if (inCart) 13.sp else 20.sp,
            fontWeight = if (inCart) FontWeight.Black else FontWeight.Normal
        )
    }
}

// ══════════════════════════════════════════════════════
// SUPPORT STRIP
// ══════════════════════════════════════════════════════
@Composable
fun SupportStrip() {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(CardShape)
            .background(PrimaryXL)
            .border(1.dp, Color(0x26FFB300), CardShape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("🏺", fontSize = 26.sp)
        Column {
            Text(
                "Support Local Artisans",
                fontSize   = 12.sp,
                fontWeight = FontWeight.Black,
                color      = Primary
            )
            Text(
                "Every purchase empowers a Western Ghats craftsman",
                fontSize   = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextLow
            )
        }
    }
}

// ══════════════════════════════════════════════════════
// BOTTOM NAV
// ══════════════════════════════════════════════════════
@Composable
fun BottomNav(activeIndex: Int, onNavSelect: (Int) -> Unit) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        color           = Color(0xF5FAFAFA),
        shadowElevation = 8.dp,
        tonalElevation  = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderColor, RoundedCornerShape(0.dp))
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                NavItem("🏠", "Home",       activeIndex == 0) { onNavSelect(0) }
                NavItem("📐", "Blueprints", activeIndex == 1) { onNavSelect(1) }
                FABCenter { onNavSelect(2) }
                NavItem("₹",  "Pricer",     activeIndex == 3) { onNavSelect(3) }
                NavItem("🛒", "Market",     activeIndex == 4) { onNavSelect(4) }
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// NAV ITEM
// ══════════════════════════════════════════════════════
@Composable
fun NavItem(icon: String, label: String, isActive: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier            = Modifier.clickable(interactionSource, null) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 40.dp)
                .clip(SmShape)
                .background(if (isActive) PrimaryXL else Color.Transparent)
                .graphicsLayer { translationY = if (isActive) -2.dp.toPx() else 0f },
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 20.sp)
        }
        Text(
            label,
            fontSize   = 9.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = if (isActive) Primary else TextLow
        )
    }
}

// ══════════════════════════════════════════════════════
// FAB CENTER (AI Assist)
// ══════════════════════════════════════════════════════
@Composable
fun FABCenter(onTrackerClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier            = Modifier.offset(y = -(4.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(BtnShape)
                .background(Accent)
                .border(3.dp, BgColor, BtnShape)
                .shadow(20.dp, BtnShape)
                .clickable(interactionSource, ripple(color = Color.White)) { onTrackerClick() }
                .padding(horizontal = 18.dp, vertical = 13.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text("✨", fontSize = 18.sp)
            Text(
                "AI Assist",
                color         = TextWhite,
                fontSize      = 11.sp,
                fontWeight    = FontWeight.Black,
                letterSpacing = 0.4.sp
            )
        }
    }
}