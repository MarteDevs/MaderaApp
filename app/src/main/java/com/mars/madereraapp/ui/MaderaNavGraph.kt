package com.mars.madereraapp.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mars.madereraapp.ui.auth.LoginScreen
import com.mars.madereraapp.ui.auth.SessionViewModel
import com.mars.madereraapp.ui.ingresos.IngresoDetalleScreen
import com.mars.madereraapp.ui.ingresos.NewIngresoScreen
import com.mars.madereraapp.ui.requerimientos.RequerimientoDetalleScreen

@Composable
fun MaderaNavGraph(
    navController: NavHostController,
    startDestination: String = "login",
    sessionViewModel: SessionViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) +
            slideInHorizontally(initialOffsetX = { it / 4 }, animationSpec = tween(300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) +
            slideInHorizontally(initialOffsetX = { -it / 4 }, animationSpec = tween(300))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200)) +
            slideOutHorizontally(targetOffsetX = { it / 4 }, animationSpec = tween(200))
        }
    ) {

        composable(
            "login",
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable(
            "main",
            enterTransition = {
                fadeIn(animationSpec = tween(400)) +
                scaleIn(initialScale = 0.95f, animationSpec = tween(400))
            }
        ) {
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
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
            enterTransition = {
                fadeIn(animationSpec = tween(350)) +
                slideInHorizontally(initialOffsetX = { it / 3 }, animationSpec = tween(350, easing = FastOutSlowInEasing))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(250)) +
                slideOutHorizontally(targetOffsetX = { it / 3 }, animationSpec = tween(250))
            }
        ) { backStack ->
            val id = backStack.arguments?.getInt("id") ?: return@composable
            RequerimientoDetalleScreen(
                requerimientoId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            "nuevo_ingreso",
            enterTransition = {
                fadeIn(animationSpec = tween(350)) +
                slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(400, easing = FastOutSlowInEasing))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(250)) +
                slideOutVertically(targetOffsetY = { it / 2 }, animationSpec = tween(300))
            }
        ) {
            NewIngresoScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            "ingreso_detalle/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
            enterTransition = {
                fadeIn(animationSpec = tween(350)) +
                slideInHorizontally(initialOffsetX = { it / 3 }, animationSpec = tween(350, easing = FastOutSlowInEasing))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(250)) +
                slideOutHorizontally(targetOffsetX = { it / 3 }, animationSpec = tween(250))
            }
        ) { backStack ->
            val id = backStack.arguments?.getInt("id") ?: return@composable
            IngresoDetalleScreen(
                ingresoId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
