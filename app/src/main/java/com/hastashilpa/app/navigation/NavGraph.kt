package com.hastashilpa.app.navigation
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hastashilpa.app.HomeScreen
import com.hastashilpa.app.ui.screens.AuthScreen
import com.hastashilpa.app.ui.screens.BlueprintScreen
import com.hastashilpa.app.ui.screens.GenAiDesignScreen
import com.hastashilpa.app.ui.screens.MarketplaceScreen
import com.hastashilpa.app.ui.screens.OnboardingScreen
import com.hastashilpa.app.ui.screens.PricerScreen
import com.hastashilpa.app.ui.screens.ProgressDashboardScreen
import com.hastashilpa.app.ui.screens.TrackerScreen

// ════════════════════════════════════════════════════════════════════════════
// SCREEN ROUTES
// ════════════════════════════════════════════════════════════════════════════

sealed class Screen(val route: String) {
    object Home        : Screen("home")
    object Blueprint   : Screen("blueprint")
    object Tracker     : Screen("tracker")
    object Pricer      : Screen("pricer")
    object Marketplace : Screen("marketplace")
    object Onboarding  : Screen("onboarding")
    object Dashboard   : Screen("dashboard")
    object GenAi       : Screen("genai")
    object Auth        : Screen("auth")
}

// ════════════════════════════════════════════════════════════════════════════
// SINGLE SOURCE OF TRUTH FOR PREFS
// File: "hs_prefs" | Keys: "onboarded", "logged_in", "is_guest"
// Used by AppNavGraph, MainActivity, and AuthScreen — all the same file+keys
// ════════════════════════════════════════════════════════════════════════════

const val PREFS_FILE   = "hs_prefs"
const val KEY_ONBOARDED = "onboarded"
const val KEY_LOGGED_IN = "logged_in"
const val KEY_IS_GUEST  = "is_guest"

// ════════════════════════════════════════════════════════════════════════════
// NAV GRAPH
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context      = LocalContext.current
    val prefs        = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
    val hasOnboarded = prefs.getBoolean(KEY_ONBOARDED, false)
    val isLoggedIn   = prefs.getBoolean(KEY_LOGGED_IN, false)

    val startDestination = when {
        !hasOnboarded -> Screen.Onboarding.route  // first ever launch
        !isLoggedIn   -> Screen.Auth.route         // seen onboarding, not logged in
        else          -> Screen.Home.route         // fully set up
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // ── Onboarding ─────────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                navController = navController,
                onFinish      = {
                    prefs.edit().putBoolean(KEY_ONBOARDED, true).apply()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Auth ───────────────────────────────────────────────────────────
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = { isGuest ->
                    prefs.edit()
                        .putBoolean(KEY_LOGGED_IN, true)
                        .putBoolean(KEY_IS_GUEST, isGuest)
                        .apply()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Main screens ───────────────────────────────────────────────────
        composable(Screen.Home.route)        { HomeScreen(navController) }
        composable(Screen.Blueprint.route)   { BlueprintScreen(navController) }
        composable(Screen.Tracker.route)     { TrackerScreen(navController) }
        composable(Screen.Pricer.route)      { PricerScreen(navController) }
        composable(Screen.Marketplace.route) { MarketplaceScreen(navController) }
        composable(Screen.Dashboard.route)   { ProgressDashboardScreen(navController) }
        composable(Screen.GenAi.route)       { GenAiDesignScreen(navController) }
    }
}