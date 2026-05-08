package com.mars.madereraapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.mars.madereraapp.di.UnauthorizedEventBus
import com.mars.madereraapp.ui.MaderaNavGraph
import com.mars.madereraapp.ui.auth.SessionViewModel
import com.mars.madereraapp.ui.theme.MadereraAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val sessionViewModel: SessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MadereraAppTheme {
                val isLoggedIn by sessionViewModel.isLoggedIn.collectAsState()
                val navController = rememberNavController()

                // Observar el event bus de 401: si el token expiró, cerrar sesión y volver al login
                LaunchedEffect(Unit) {
                    UnauthorizedEventBus.events.collectLatest {
                        sessionViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

                when (isLoggedIn) {
                    null -> {
                        // Splash: verificando sesión guardada
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    true -> {
                        MaderaNavGraph(
                            navController = navController,
                            startDestination = "main",
                            sessionViewModel = sessionViewModel
                        )
                    }
                    false -> {
                        MaderaNavGraph(
                            navController = navController,
                            startDestination = "login",
                            sessionViewModel = sessionViewModel
                        )
                    }
                }
            }
        }
    }
}