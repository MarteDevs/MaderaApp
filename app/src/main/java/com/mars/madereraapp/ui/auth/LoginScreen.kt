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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
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
                shakeOffset = 8f
                delay(50)
                shakeOffset = -8f
                delay(50)
            }
            shakeOffset = 0f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
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
                enter = fadeIn(tween(600)) + scaleIn(initialScale = 0.9f, animationSpec = tween(600))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_madera),
                        contentDescription = "Logo Madera Poltand",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Madera Poltand",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sistema de Gestión v1.5",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 40.dp)
                    )
                }
            }

            // Animated Form Card
            AnimatedVisibility(
                visible = formVisible,
                enter = fadeIn(tween(500)) + slideInVertically(
                    initialOffsetY = { it / 5 },
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                )
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)),
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceLight,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        // User field
                        GlassTextField(
                            value = viewModel.usuario,
                            onValueChange = { viewModel.usuario = it.uppercase() },
                            label = "Usuario",
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(20.dp))
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password field
                        GlassTextField(
                            value = viewModel.clave,
                            onValueChange = { viewModel.clave = it.uppercase() },
                            label = "Contraseña",
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(20.dp))
                            },
                            trailingIcon = {
                                val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = icon, contentDescription = null, tint = TextTertiary)
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
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = ColorRejected.copy(alpha = 0.08f)
                            ) {
                                Text(
                                    text = viewModel.error ?: "",
                                    color = ColorRejected,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Login button
                        IndustrialButton(
                            onClick = { viewModel.onLoginClick() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !viewModel.cargando
                        ) {
                            if (viewModel.cargando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = TextOnPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Iniciar Sesión",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Version footer
        Text(
            text = "© 2026 Madera Poltand",
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
