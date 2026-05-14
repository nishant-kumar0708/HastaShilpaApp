package com.hastashilpa.app.viewmodel
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hastashilpa.app.data.AuthRepository
import com.hastashilpa.app.data.BatchDatabase
import com.hastashilpa.app.data.UserEntity
import com.hastashilpa.app.navigation.KEY_IS_GUEST
import com.hastashilpa.app.navigation.KEY_LOGGED_IN
import com.hastashilpa.app.navigation.PREFS_FILE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle    : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserEntity) : AuthState()
    data class Error(val message: String)   : AuthState()
}

enum class LoginTab { EMAIL, PHONE }

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AuthRepository(
        BatchDatabase.getDatabase(app).userDao()
    )

    private val prefs = app.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Form fields
    val name            = MutableStateFlow("")
    val email           = MutableStateFlow("")
    val phone           = MutableStateFlow("")
    val password        = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")
    val craft           = MutableStateFlow("Bamboo & Cane")
    val location        = MutableStateFlow("")
    val activeTab       = MutableStateFlow(LoginTab.EMAIL)

    // ── KEY FIX: Load user from Room DB on ViewModel creation ─────────────
    // This runs every time the app starts — ensures currentUser is never null
    // even after app restart (as long as user is logged in)
    init {
        val savedUserId = prefs.getInt("logged_in_user_id", -1)
        val isLoggedIn  = prefs.getBoolean(KEY_LOGGED_IN, false)
        if (isLoggedIn && savedUserId != -1) {
            viewModelScope.launch {
                val user = repo.getUserById(savedUserId)
                if (user != null) _currentUser.value = user
            }
        }
    }

    fun loginWithEmail() = runAction {
        repo.loginWithEmail(email.value, password.value)
    }

    fun loginWithPhone() = runAction {
        repo.loginWithPhone(phone.value, password.value)
    }

    fun registerWithEmail() {
        if (password.value != confirmPassword.value) {
            _state.value = AuthState.Error("Passwords do not match"); return
        }
        if (password.value.length < 6) {
            _state.value = AuthState.Error("Password must be at least 6 characters"); return
        }
        runAction {
            repo.registerWithEmail(
                name.value, email.value, password.value,
                craft.value, location.value
            )
        }
    }

    fun registerWithPhone() {
        if (password.value != confirmPassword.value) {
            _state.value = AuthState.Error("Passwords do not match"); return
        }
        runAction { repo.registerWithPhone(name.value, phone.value, password.value) }
    }

    fun continueAsGuest() {
        _state.value = AuthState.Success(repo.createGuestUser())
    }

    fun signInWithGoogle() {
        _state.value = AuthState.Error("Google Sign-In coming soon — use email or phone for now")
    }

    fun logout() {
        prefs.edit()
            .putBoolean(KEY_LOGGED_IN, false)
            .putBoolean(KEY_IS_GUEST, false)
            .putInt("logged_in_user_id", -1)
            .apply()
        _currentUser.value = null
        _state.value       = AuthState.Idle
        email.value           = ""
        password.value        = ""
        phone.value           = ""
        name.value            = ""
        confirmPassword.value = ""
        location.value        = ""
    }

    fun resetState() { _state.value = AuthState.Idle }

    private fun runAction(block: suspend () -> Result<UserEntity>) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            val result = block()
            _state.value = result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    // Save user ID to prefs so we can reload from Room on restart
                    prefs.edit().putInt("logged_in_user_id", user.id).apply()
                    AuthState.Success(user)
                },
                onFailure = { AuthState.Error(it.message ?: "Something went wrong") }
            )
        }
    }
}