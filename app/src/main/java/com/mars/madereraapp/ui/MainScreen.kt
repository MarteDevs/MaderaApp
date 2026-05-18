package com.mars.madereraapp.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.R
import com.mars.madereraapp.ui.auth.SessionViewModel
import com.mars.madereraapp.ui.components.*
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
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            if (selectedTab == 0) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_madera),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("MADERA POLTAND", style = MaterialTheme.typography.titleLarge, color = PrimaryAmber)
                                Text("INDUSTRIAL SYSTEM", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark),
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
                containerColor = BackgroundDark,
                tonalElevation = 0.dp,
                modifier = Modifier.border(1.dp, GlassWhite, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                val tabs = listOf(
                    Triple(0, Icons.Default.Home, "Inicio"),
                    Triple(1, Icons.Default.ListAlt, "Reqs"),
                    Triple(2, Icons.Default.Inventory, "Ingresos")
                )

                tabs.forEach { (index, icon, label) ->
                    val isSelected = selectedTab == index
                    val iconScale by animateFloatAsState(if (isSelected) 1.2f else 1f)
                    
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                icon, 
                                contentDescription = label,
                                modifier = Modifier.graphicsLayer(scaleX = iconScale, scaleY = iconScale)
                            ) 
                        },
                        label = { Text(label) },
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryAmber,
                            selectedTextColor = PrimaryAmber,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = GlassWhite
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF000000), Color(0xFF131313))
                )
            )) {
            
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                    } else {
                        (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                    }.using(SizeTransform(clip = false))
                },
                label = "TabTransition"
            ) { targetTab ->
                when (targetTab) {
                    0 -> DashboardTab(requerimientoViewModel)
                    1 -> RequerimientoListScreen(onNavigateToDetail = onNavigateToRequerimientoDetalle, viewModel = requerimientoViewModel)
                    2 -> IngresoListScreen(onNavigateToCreate = onNavigateToNuevoIngreso, onNavigateToDetail = onNavigateToIngresoDetalle)
                }
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

    var startAnimations by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { startAnimations = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "RESUMEN OPERATIVO",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )

        AnimatedVisibility(
            visible = startAnimations,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(initialOffsetY = { 40 })
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Requerimientos",
                    value = total,
                    color = PrimaryAmber
                )
                DashboardMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Pendientes",
                    value = pendientes,
                    color = ColorPending
                )
            }
        }

        AnimatedVisibility(
            visible = startAnimations,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) + slideInVertically(initialOffsetY = { 40 })
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Atención Parcial",
                    value = parciales,
                    color = StatusParcial
                )
                DashboardMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Completados",
                    value = completados,
                    color = ColorApproved
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        
        Image(
            painter = painterResource(id = R.drawable.logo_madera),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(12.dp))
                .graphicsLayer(alpha = 0.3f)
        )
        
        Text(
            text = "INDUSTRIAL ERP • PREMIUM EDITION",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary.copy(alpha = 0.2f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun DashboardMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: Int,
    color: Color
) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
    )

    GlassCard(
        modifier = modifier.height(140.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Text(
                animatedValue.toString(),
                style = MaterialTheme.typography.headlineLarge,
                color = color
            )
        }
    }
}
