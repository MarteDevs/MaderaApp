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
import com.mars.madereraapp.ui.ingresos.IngresoListScreen
import com.mars.madereraapp.ui.requerimientos.NewRequerimientoScreen
import com.mars.madereraapp.ui.requerimientos.RequerimientoDetalleScreen
import com.mars.madereraapp.ui.requerimientos.RequerimientoListScreen
import com.mars.madereraapp.ui.theme.BackgroundDark
import com.mars.madereraapp.ui.theme.BorderColor
import com.mars.madereraapp.ui.theme.PrimaryBlue
import com.mars.madereraapp.ui.theme.SurfaceDark
import com.mars.madereraapp.ui.theme.SurfaceVariant
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
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                sessionViewModel = sessionViewModel,
                onNavigateToRequerimientos = { navController.navigate("requerimientos") },
                onNavigateToIngresos = { navController.navigate("ingresos") },
                onLogout = {
                    sessionViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("requerimientos") {
            RequerimientoListScreen(
                onNavigateToCreate = { navController.navigate("nuevo_requerimiento") },
                onNavigateToDetail = { id -> navController.navigate("requerimiento_detalle/$id") }
            )
        }

        composable("nuevo_requerimiento") {
            NewRequerimientoScreen(onNavigateBack = { navController.popBackStack() })
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

        composable("ingresos") {
            IngresoListScreen(
                onNavigateToCreate = { navController.navigate("nuevo_ingreso") },
                onNavigateToDetail = { id -> navController.navigate("ingreso_detalle/$id") }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    sessionViewModel: SessionViewModel,
    onNavigateToRequerimientos: () -> Unit,
    onNavigateToIngresos: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Madera Poltand",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            "Sistema ERP",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Cerrar Sesión",
                            tint = TextSecondary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Panel de Control",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                letterSpacing = 0.8.sp
            )

            DashboardCard(
                title = "Requerimientos",
                subtitle = "Ver y gestionar pedidos de mina",
                icon = Icons.Default.ListAlt,
                gradientColors = listOf(Color(0xFF1D4ED8), Color(0xFF3B82F6)),
                onClick = onNavigateToRequerimientos
            )

            DashboardCard(
                title = "Ingresos de Stock",
                subtitle = "Registrar entregas de material",
                icon = Icons.Default.Inventory,
                gradientColors = listOf(Color(0xFF065F46), Color(0xFF10B981)),
                onClick = onNavigateToIngresos
            )

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Text(
                text = "v1.0  •  Offline First",
                fontSize = 11.sp,
                color = TextSecondary.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}
