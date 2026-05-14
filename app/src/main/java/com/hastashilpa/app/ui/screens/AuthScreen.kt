package com.hastashilpa.app.ui.screens
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hastashilpa.app.viewmodel.AuthState
import com.hastashilpa.app.viewmodel.AuthViewModel
import com.hastashilpa.app.viewmodel.LoginTab

// ── Design tokens ─────────────────────────────────────────────
private val Green800  = Color(0xFF2E7D32)
private val Green600  = Color(0xFF388E3C)
private val Cream     = Color(0xFFFFF8EF)
private val Stone     = Color(0xFFF0EBE0)
private val TextDark  = Color(0xFF1C1917)
private val TextMid   = Color(0xFF57534E)
private val TextLight = Color(0xFF9CA3AF)
private val ErrorRed  = Color(0xFFB91C1C)
private val ErrorBg   = Color(0xFFFEF2F2)

// ══════════════════════════════════════════════════════════════
@Composable
fun AuthScreen(
    onAuthSuccess: (isGuest: Boolean) -> Unit,
    vm: AuthViewModel = viewModel()
) {
    var isSignIn       by remember { mutableStateOf(true) }
    var pwVisible      by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    val state     by vm.state.collectAsState()
    val name      by vm.name.collectAsState()
    val email     by vm.email.collectAsState()
    val phone     by vm.phone.collectAsState()
    val password  by vm.password.collectAsState()
    val confirmPw by vm.confirmPassword.collectAsState()
    val location  by vm.location.collectAsState()
    val activeTab by vm.activeTab.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            val isGuest = (state as AuthState.Success).user.loginMethod == "GUEST"
            onAuthSuccess(isGuest)
            vm.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B5E20))
            .drawBehind {
                drawCircle(
                    color  = Color.White.copy(alpha = 0.04f),
                    radius = 320.dp.toPx(),
                    center = Offset(-80.dp.toPx(), -80.dp.toPx())
                )
                drawCircle(
                    color  = Color(0xFFFF8F00).copy(alpha = 0.10f),
                    radius = 200.dp.toPx(),
                    center = Offset(size.width + 60.dp.toPx(), 100.dp.toPx())
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            // ── Brand mark ────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(
                        Brush.radialGradient(listOf(Color(0xFF43A047), Green800)),
                        CircleShape
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) { Text("🎋", fontSize = 34.sp) }

            Spacer(Modifier.height(14.dp))
            Text(
                "Hasta-Shilpa",
                color      = Color.White,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "AI-Powered · Design · Sell · Learn",
                color    = Color.White.copy(alpha = 0.65f),
                fontSize = 12.sp
            )
            Spacer(Modifier.height(28.dp))

            // ── Card ──────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                shape     = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
                colors    = CardDefaults.cardColors(containerColor = Cream),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(Modifier.padding(horizontal = 22.dp, vertical = 22.dp)) {

                    // ── Sign In / Register toggle ──────────────
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Stone)
                            .padding(3.dp)
                    ) {
                        listOf("Sign In" to true, "Register" to false).forEach { (label, isLogin) ->
                            val selected = isLogin == isSignIn
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) Green800 else Color.Transparent)
                                    .clickable { isSignIn = isLogin; vm.resetState() }
                                    .padding(vertical = 11.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    label,
                                    color      = if (selected) Color.White else TextMid,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    fontSize   = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // ── Email / Phone tab ──────────────────────
                    Row(Modifier.fillMaxWidth()) {
                        listOf(LoginTab.EMAIL to "Email", LoginTab.PHONE to "Phone").forEach { (tab, label) ->
                            val selected = tab == activeTab
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { vm.activeTab.value = tab; vm.resetState() }
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    label,
                                    fontSize   = 13.sp,
                                    color      = if (selected) Green800 else TextLight,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    Modifier
                                        .fillMaxWidth(0.5f)
                                        .height(2.dp)
                                        .background(
                                            if (selected) Green800 else Color.Transparent,
                                            RoundedCornerShape(1.dp)
                                        )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // ── Error banner ───────────────────────────
                    AnimatedVisibility(
                        visible = state is AuthState.Error,
                        enter   = fadeIn() + expandVertically(),
                        exit    = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ErrorBg)
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint     = ErrorRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    (state as? AuthState.Error)?.message ?: "",
                                    color    = ErrorRed,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                        }
                    }

                    // ── Register-only fields ───────────────────
                    AnimatedVisibility(
                        visible = !isSignIn,
                        enter   = fadeIn() + expandVertically(),
                        exit    = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            HsTextField(
                                value         = name,
                                onValueChange = { vm.name.value = it },
                                label         = "Full Name",
                                placeholder   = "e.g. Kaveri Nair",
                                icon          = Icons.Default.Person
                            )
                            Spacer(Modifier.height(11.dp))
                            HsTextField(
                                value         = location,
                                onValueChange = { vm.location.value = it },
                                label         = "Village / District",
                                placeholder   = "e.g. Wayanad, Kerala",
                                icon          = Icons.Default.LocationOn
                            )
                            Spacer(Modifier.height(11.dp))
                        }
                    }

                    // ── Email or Phone ─────────────────────────
                    AnimatedContent(
                        targetState    = activeTab,
                        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                        label          = "tab"
                    ) { tab ->
                        when (tab) {
                            LoginTab.EMAIL -> HsTextField(
                                value         = email,
                                onValueChange = { vm.email.value = it },
                                label         = "Email Address",
                                placeholder   = "artisan@example.com",
                                icon          = Icons.Default.Email,
                                keyboardType  = KeyboardType.Email
                            )
                            LoginTab.PHONE -> HsTextField(
                                value         = phone,
                                onValueChange = {
                                    if (it.length <= 10 && it.all(Char::isDigit))
                                        vm.phone.value = it
                                },
                                label        = "Mobile Number",
                                placeholder  = "10-digit number",
                                icon         = Icons.Default.Phone,
                                keyboardType = KeyboardType.Phone,
                                prefix       = "+91 "
                            )
                        }
                    }

                    Spacer(Modifier.height(11.dp))

                    // ── Password ───────────────────────────────
                    HsPasswordField(
                        value         = password,
                        onValueChange = { vm.password.value = it },
                        label         = "Password",
                        visible       = pwVisible,
                        onToggle      = { pwVisible = !pwVisible }
                    )

                    // ── Confirm password (register only) ───────
                    AnimatedVisibility(!isSignIn) {
                        Column {
                            Spacer(Modifier.height(11.dp))
                            HsPasswordField(
                                value         = confirmPw,
                                onValueChange = { vm.confirmPassword.value = it },
                                label         = "Confirm Password",
                                visible       = confirmVisible,
                                onToggle      = { confirmVisible = !confirmVisible }
                            )
                        }
                    }

                    if (isSignIn) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(
                                onClick        = { },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Forgot password?", color = Green800, fontSize = 12.sp)
                            }
                        }
                    } else {
                        Spacer(Modifier.height(14.dp))
                    }

                    // ── Primary button ─────────────────────────
                    Button(
                        onClick = {
                            if (isSignIn) {
                                if (activeTab == LoginTab.EMAIL) vm.loginWithEmail()
                                else vm.loginWithPhone()
                            } else {
                                if (activeTab == LoginTab.EMAIL) vm.registerWithEmail()
                                else vm.registerWithPhone()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = Green800),
                        enabled  = state !is AuthState.Loading
                    ) {
                        if (state is AuthState.Loading) {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(20.dp),
                                color       = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                if (isSignIn) "Sign In" else "Create Account",
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ── Divider ────────────────────────────────
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        HorizontalDivider(Modifier.weight(1f), color = Color(0xFFDDD8CF))
                        Text("  or continue with  ", color = TextLight, fontSize = 11.sp)
                        HorizontalDivider(Modifier.weight(1f), color = Color(0xFFDDD8CF))
                    }

                    Spacer(Modifier.height(12.dp))

                    // ── Google button ──────────────────────────
                    OutlinedButton(
                        onClick  = { vm.signInWithGoogle() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape    = RoundedCornerShape(12.dp),
                        border   = BorderStroke(1.dp, Color(0xFFDDD8CF)),
                        colors   = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                    ) {
                        Text("G", color = Color(0xFF4285F4), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(10.dp))
                        Text("Continue with Google", color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(Modifier.height(8.dp))

                    // ── Guest button ───────────────────────────
                    TextButton(
                        onClick  = { vm.continueAsGuest() },
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("👤", fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        Text("Browse as Guest", color = TextMid, fontSize = 13.sp)
                    }
                }
            }

            // ── Footer ────────────────────────────────────────
            Spacer(Modifier.height(18.dp))
            Text(
                "🔒 Your data stays private · AES-256 encrypted",
                color    = Color.White.copy(alpha = 0.40f),
                fontSize = 11.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "🌿 Empowering bamboo & cane artisans\nacross Western Ghats, India",
                color      = Color.White.copy(alpha = 0.30f),
                fontSize   = 11.sp,
                textAlign  = TextAlign.Center,
                lineHeight = 17.sp
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}

// ══════════════════════════════════════════════════════════════
// REUSABLE TEXT FIELD
// ══════════════════════════════════════════════════════════════
@Composable
private fun HsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    prefix: String = ""
) {
    OutlinedTextField(
        value           = value,
        onValueChange   = onValueChange,
        label           = { Text(label, fontSize = 12.sp) },
        placeholder     = { Text(placeholder, fontSize = 13.sp, color = Color(0xFFBDBDBD)) },
        leadingIcon     = { Icon(icon, null, tint = Green600, modifier = Modifier.size(18.dp)) },
        prefix          = if (prefix.isNotEmpty()) {
            { Text(prefix, fontSize = 13.sp, color = Green600, fontWeight = FontWeight.Medium) }
        } else null,
        singleLine      = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(12.dp),
        colors          = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = Green800,
            unfocusedBorderColor    = Color(0xFFDDD8CF),
            focusedLabelColor       = Green800,
            unfocusedLabelColor     = TextLight,
            focusedTextColor        = TextDark,
            unfocusedTextColor      = TextDark,
            focusedContainerColor   = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

// ══════════════════════════════════════════════════════════════
// REUSABLE PASSWORD FIELD
// Uses text "SHOW"/"HIDE" toggle — no extended icon dependency
// ══════════════════════════════════════════════════════════════
@Composable
private fun HsPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggle: () -> Unit
) {
    OutlinedTextField(
        value                = value,
        onValueChange        = onValueChange,
        label                = { Text(label, fontSize = 12.sp) },
        leadingIcon          = {
            Icon(Icons.Default.Lock, null, tint = Green600, modifier = Modifier.size(18.dp))
        },
        trailingIcon         = {
            // Text toggle — works with ANY Material3 version, no extended icons needed
            TextButton(
                onClick        = onToggle,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Text(
                    text       = if (visible) "HIDE" else "SHOW",
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Green800
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine           = true,
        modifier             = Modifier.fillMaxWidth(),
        shape                = RoundedCornerShape(12.dp),
        colors               = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = Green800,
            unfocusedBorderColor    = Color(0xFFDDD8CF),
            focusedLabelColor       = Green800,
            unfocusedLabelColor     = TextLight,
            focusedTextColor        = TextDark,
            unfocusedTextColor      = TextDark,
            focusedContainerColor   = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}