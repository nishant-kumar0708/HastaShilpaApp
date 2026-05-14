package com.hastashilpa.app.util
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


object SecurePrefs {

    fun get(context: Context): android.content.SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "hastashilpa_secure",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun savePricingResult(context: Context, productName: String, price: Int) {
        get(context).edit()
            .putInt("price_$productName", price)
            .apply()
    }

    fun getPricingResult(context: Context, productName: String): Int {
        return get(context).getInt("price_$productName", 0)
    }
}