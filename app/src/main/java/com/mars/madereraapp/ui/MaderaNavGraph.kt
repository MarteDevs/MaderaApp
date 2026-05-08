package com.mars.madereraapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mars.madereraapp.ui.auth.LoginScreen
import com.mars.madereraapp.ui.ingresos.IngresoListScreen
import com.mars.madereraapp.ui.ingresos.NewIngresoScreen
import com.mars.madereraapp.ui.requerimientos.NewRequerimientoScreen
import com.mars.madereraapp.ui.requerimientos.RequerimientoListScreen

@Composable
fun MaderaNavGraph(
    navController: NavHostController,
    startDestination: String = "login"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                onNavigateToRequerimientos = {
                    navController.navigate("requerimientos")
                },
                onNavigateToIngresos = {
                    navController.navigate("ingresos")
                }
            )
        }
        composable("requerimientos") {
            RequerimientoListScreen(
                onNavigateToCreate = {
                    navController.navigate("nuevo_requerimiento")
                }
            )
        }
        composable("nuevo_requerimiento") {
            NewRequerimientoScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("ingresos") {
            IngresoListScreen(
                onNavigateToCreate = {
                    navController.navigate("nuevo_ingreso")
                }
            )
        }
        composable("nuevo_ingreso") {
            NewIngresoScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToRequerimientos: () -> Unit,
    onNavigateToIngresos: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Madera Poltand ERP", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedCard(
                onClick = onNavigateToRequerimientos,
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Gestionar Requerimientos", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
            
            ElevatedCard(
                onClick = onNavigateToIngresos,
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondary),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSecondary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Registrar Ingresos", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSecondary)
                    }
                }
            }
        }
    }
}
