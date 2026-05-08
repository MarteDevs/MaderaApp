package com.mars.madereraapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.ui.auth.SessionViewModel
import com.mars.madereraapp.ui.ingresos.IngresoListScreen
import com.mars.madereraapp.ui.requerimientos.RequerimientoListScreen
import com.mars.madereraapp.ui.requerimientos.RequerimientoViewModel
import com.mars.madereraapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToRequerimientoDetalle: (Int) -> Unit,
    onNavigateToIngresoDetalle: (Int) -> Unit,
    onNavigateToNuevoIngreso: () -> Unit,
    onLogout: () -> Unit,
    sessionViewModel: SessionViewModel,
    requerimientoViewModel: RequerimientoViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            if (selectedTab == 0) { // Dashboard tiene su propio TopBar especial
                TopAppBar(
                    title = {
                        Column {
                            Text("Madera Poltand", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = TextPrimary)
                            Text("Sistema ERP", fontSize = 12.sp, color = TextSecondary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark),
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.Logout, contentDescription = "Cerrar Sesión", tint = TextSecondary)
                        }
                    }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = SurfaceVariant
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = "Reqs") },
                    label = { Text("Reqs") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = SurfaceVariant
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Inventory, contentDescription = "Ingresos") },
                    label = { Text("Ingresos") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = SurfaceVariant
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> DashboardTab(requerimientoViewModel)
                1 -> RequerimientoListScreen(onNavigateToDetail = onNavigateToRequerimientoDetalle, viewModel = requerimientoViewModel)
                2 -> IngresoListScreen(onNavigateToCreate = onNavigateToNuevoIngreso, onNavigateToDetail = onNavigateToIngresoDetalle)
            }
        }
    }
}

@Composable
fun DashboardTab(viewModel: RequerimientoViewModel) {
    val total by viewModel.totalRequerimientos.collectAsState()
    val pendientes by viewModel.pendientes.collectAsState()
    val parciales by viewModel.parciales.collectAsState()
    val completados by viewModel.completados.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Resumen General",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardMetricCard(
                modifier = Modifier.weight(1f),
                title = "Total Requerimientos",
                value = total.toString(),
                color = PrimaryBlue
            )
            DashboardMetricCard(
                modifier = Modifier.weight(1f),
                title = "Pendientes",
                value = pendientes.toString(),
                color = StatusPendiente
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardMetricCard(
                modifier = Modifier.weight(1f),
                title = "Atención Parcial",
                value = parciales.toString(),
                color = StatusParcial
            )
            DashboardMetricCard(
                modifier = Modifier.weight(1f),
                title = "Completados",
                value = completados.toString(),
                color = StatusCompletado
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "v1.0  •  Offline First",
            fontSize = 11.sp,
            color = TextSecondary.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun DashboardMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
            Text(
                value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
