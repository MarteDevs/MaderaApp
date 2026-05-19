package com.mars.madereraapp.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.R
import com.mars.madereraapp.ui.components.*
import com.mars.madereraapp.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var shakeOffset by remember { mutableStateOf(0f) }
    val focusManager = LocalFocusManager.current

    // Logo entrance animation
    var logoVisible by remember { mutableStateOf(false) }
    var formVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.loginSuccess.collect { onLoginSuccess() }
    }
    LaunchedEffect(Unit) {
        delay(200)
        logoVisible = true
        delay(400)
        formVisible = true
    }

    // Shake animation for error
    LaunchedEffect(viewModel.error) {
        if (viewModel.error != null) {
            repeat(6) {
                shakeOffset = 10f
                delay(50)
                shakeOffset = -10f
                delay(50)
            }
            shakeOffset = 0f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF000000), Color(0xFF0A0A0A), Color(0xFF131313))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 32.dp)
                .graphicsLayer(translationX = shakeOffset),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated Logo
            AnimatedVisibility(
                visible = logoVisible,
                enter = fadeIn(tween(800)) + scaleIn(initialScale = 0.8f, animationSpec = tween(800))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_madera),
                        contentDescription = "Logo Madera Poltand",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "MADERA POLTAND",
                        style = MaterialTheme.typography.headlineSmall,
                        color = PrimaryAmber,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "INDUSTRIAL ERP SYSTEM v1.5",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 48.dp)
                    )
                }
            }

            // Animated Form Card
            AnimatedVisibility(
                visible = formVisible,
                enter = fadeIn(tween(600)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                )
            ) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy))
                ) {
                    Column {
                        // User field — using GlassTextField
                        GlassTextField(
                            value = viewModel.usuario,
                            onValueChange = { viewModel.usuario = it.uppercase() },
                            label = "Usuario",
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password field — using GlassTextField
                        GlassTextField(
                            value = viewModel.clave,
                            onValueChange = { viewModel.clave = it.uppercase() },
                            label = "Contraseña",
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                            },
                            trailingIcon = {
                                val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = icon, contentDescription = null, tint = TextSecondary)
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                                viewModel.onLoginClick()
                            })
                        )

                        // Animated error
                        AnimatedVisibility(
                            visible = viewModel.error != null,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Text(
                                text = viewModel.error ?: "",
                                color = ColorRejected,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Industrial login button
                        IndustrialButton(
                            onClick = { viewModel.onLoginClick() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !viewModel.cargando
                        ) {
                            if (viewModel.cargando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = TextOnPrimary,
                                    strokeWidth = 3.dp
                                )
                            } else {
                                Text(
                                    text = "ACCEDER AL SISTEMA",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Version footer
        Text(
            text = "© 2026 Madera Poltand • Todos los derechos reservados",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary.copy(alpha = 0.2f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
