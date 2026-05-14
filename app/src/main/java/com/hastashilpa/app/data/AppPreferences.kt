package com.hastashilpa.app.data

import android.content.Context

class AppPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("hasta_prefs", Context.MODE_PRIVATE)

    // ── Existing preferences (untouched) ──────────────────────
    var labourRatePerHour: Int
        get() = prefs.getInt("labour_rate", 80)
        set(value) = prefs.edit().putInt("labour_rate", value).apply()

    var overheadPercent: Int
        get() = prefs.getInt("overhead_pct", 20)
        set(value) = prefs.edit().putInt("overhead_pct", value).apply()

    // ── Onboarding ────────────────────────────────────────────
    fun isOnboarded(): Boolean = prefs.getBoolean("is_onboarded", false)
    fun setOnboarded(value: Boolean) =
        prefs.edit().putBoolean("is_onboarded", value).apply()

    // ── Auth / Login state ────────────────────────────────────
    fun isLoggedIn(): Boolean =
        prefs.getBoolean("is_logged_in", false)

    fun setLoggedIn(value: Boolean) =
        prefs.edit().putBoolean("is_logged_in", value).apply()

    fun saveLoggedInUserId(id: Int) =
        prefs.edit().putInt("logged_in_user_id", id).apply()

    fun getLoggedInUserId(): Int =
        prefs.getInt("logged_in_user_id", -1)

    // ── Guest session ─────────────────────────────────────────
    fun isGuestSession(): Boolean =
        prefs.getBoolean("is_guest", false)

    fun setGuestSession(value: Boolean) =
        prefs.edit().putBoolean("is_guest", value).apply()

    // ── Logout — clears auth state, keeps onboarding flag ─────
    fun logout() {
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .putBoolean("is_guest", false)
            .putInt("logged_in_user_id", -1)
            .apply()
    }
}