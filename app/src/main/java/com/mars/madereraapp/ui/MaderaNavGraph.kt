package com.mars.madereraapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mars.madereraapp.ui.auth.LoginScreen
import com.mars.madereraapp.ui.auth.SessionViewModel
import com.mars.madereraapp.ui.ingresos.IngresoDetalleScreen
import com.mars.madereraapp.ui.requerimientos.RequerimientoDetalleScreen
import com.mars.madereraapp.ui.theme.BackgroundDark
import com.mars.madereraapp.ui.theme.SurfaceDark
import com.mars.madereraapp.ui.theme.TextPrimary
import com.mars.madereraapp.ui.theme.TextSecondary
import com.mars.madereraapp.ui.ingresos.NewIngresoScreen

@Composable
fun MaderaNavGraph(
    navController: NavHostController,
    startDestination: String = "login",
    sessionViewModel: SessionViewModel
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(
                sessionViewModel = sessionViewModel,
                onNavigateToRequerimientoDetalle = { id -> navController.navigate("requerimiento_detalle/$id") },
                onNavigateToIngresoDetalle = { id -> navController.navigate("ingreso_detalle/$id") },
                onNavigateToNuevoIngreso = { navController.navigate("nuevo_ingreso") },
                onLogout = {
                    sessionViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            "requerimiento_detalle/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStack ->
            val id = backStack.arguments?.getInt("id") ?: return@composable
            RequerimientoDetalleScreen(
                requerimientoId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("nuevo_ingreso") {
            NewIngresoScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            "ingreso_detalle/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStack ->
            val id = backStack.arguments?.getInt("id") ?: return@composable
            IngresoDetalleScreen(
                ingresoId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
