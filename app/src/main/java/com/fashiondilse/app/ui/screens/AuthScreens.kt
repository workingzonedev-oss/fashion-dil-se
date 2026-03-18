package com.fashiondilse.app.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fashiondilse.app.ui.components.FashionField
import com.fashiondilse.app.ui.components.FashionLogoBadge
import com.fashiondilse.app.ui.components.HeaderBar
import com.fashiondilse.app.ui.components.PrimaryCtaButton
import com.fashiondilse.app.ui.components.ScreenPadding
import com.fashiondilse.app.ui.components.SecondaryCtaButton
import com.fashiondilse.app.ui.theme.SoftGold
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(900))
    }
    LaunchedEffect(Unit) {
        delay(1600)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                    ),
                ),
            )
            .statusBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value),
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = MaterialTheme.shapes.extraLarge,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                FashionLogoBadge()
            }
            Text(
                text = "Fashion Dil Se",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Dil se fashion, har budget mein",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun WelcomeScreen(
    onLogin: () -> Unit,
    onCreateAccount: () -> Unit,
    onGuest: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                            SoftGold,
                        ),
                    ),
                    shape = MaterialTheme.shapes.large,
                )
                .padding(24.dp),
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Affordable fashion, elevated with a cleaner premium mood.",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = "Dil se fashion, har budget mein.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                )
            }
        }

        FashionLogoBadge()

        PrimaryCtaButton(text = "Login", onClick = onLogin)
        SecondaryCtaButton(text = "Create Account", onClick = onCreateAccount)
        SecondaryCtaButton(text = "Continue as Guest", onClick = onGuest)
    }
}

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onCreateAccount: () -> Unit,
) {
    val context = LocalContext.current
    var mobile by rememberSaveable { mutableStateOf("") }
    var otp by rememberSaveable { mutableStateOf("") }
    var otpSent by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        HeaderBar(title = "Login", showBack = true, onBack = onBack)

        Text(
            text = "Enter your mobile number to get started",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        FashionField(
            value = mobile,
            onValueChange = { if (it.length <= 10) mobile = it.filter { c -> c.isDigit() } },
            label = "Mobile Number",
        )

        if (otpSent) {
            Text(
                text = "We\u2019ve sent a 6-digit OTP to +91 $mobile",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            FashionField(
                value = otp,
                onValueChange = { if (it.length <= 6) otp = it.filter { c -> c.isDigit() } },
                label = "Enter OTP",
            )
            PrimaryCtaButton(
                text = "Verify & Login",
                onClick = {
                    if (otp.length == 6) {
                        onLoginSuccess()
                    } else {
                        Toast.makeText(context, "Please enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show()
                    }
                },
            )
            SecondaryCtaButton(
                text = "Resend OTP",
                onClick = {
                    Toast.makeText(context, "OTP resent to +91 $mobile", Toast.LENGTH_SHORT).show()
                },
            )
        } else {
            PrimaryCtaButton(
                text = "Send OTP",
                onClick = {
                    if (mobile.length == 10) {
                        otpSent = true
                        Toast.makeText(context, "OTP sent to +91 $mobile", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                    }
                },
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "New here?",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
        SecondaryCtaButton(text = "Create New Account", onClick = onCreateAccount)
    }
}

@Composable
fun SignUpScreen(
    onBack: () -> Unit,
    onCreateAccount: () -> Unit,
    onLogin: () -> Unit,
) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var mobile by rememberSaveable { mutableStateOf("") }
    var otp by rememberSaveable { mutableStateOf("") }
    var otpSent by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        HeaderBar(title = "Create Account", showBack = true, onBack = onBack)

        FashionField(value = name, onValueChange = { name = it }, label = "Full Name")
        FashionField(
            value = mobile,
            onValueChange = { if (it.length <= 10) mobile = it.filter { c -> c.isDigit() } },
            label = "Mobile Number",
        )

        if (otpSent) {
            Text(
                text = "We\u2019ve sent a 6-digit OTP to +91 $mobile",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            FashionField(
                value = otp,
                onValueChange = { if (it.length <= 6) otp = it.filter { c -> c.isDigit() } },
                label = "Enter OTP",
            )
            PrimaryCtaButton(
                text = "Verify & Create Account",
                onClick = {
                    if (otp.length == 6 && name.isNotBlank()) {
                        onCreateAccount()
                    } else {
                        Toast.makeText(context, "Please fill all fields and enter a valid OTP", Toast.LENGTH_SHORT).show()
                    }
                },
            )
            SecondaryCtaButton(
                text = "Resend OTP",
                onClick = {
                    Toast.makeText(context, "OTP resent to +91 $mobile", Toast.LENGTH_SHORT).show()
                },
            )
        } else {
            PrimaryCtaButton(
                text = "Send OTP",
                onClick = {
                    if (mobile.length == 10 && name.isNotBlank()) {
                        otpSent = true
                        Toast.makeText(context, "OTP sent to +91 $mobile", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Please enter your name and a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                    }
                },
            )
        }

        SecondaryCtaButton(text = "Already have an account? Login", onClick = onLogin)

        Spacer(modifier = Modifier.height(12.dp))
    }
}
