package com.mars.madereraapp.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Connectivity indicator
    val context = androidx.compose.ui.platform.LocalContext.current
    val connectivityManager = remember {
        context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
    }
    var isOnline by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            isOnline = capabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            kotlinx.coroutines.delay(5000)
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = SurfaceLight,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Cerrar Sesión", fontWeight = FontWeight.SemiBold) },
            text = { Text("¿Estás seguro de que deseas salir del sistema?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorRejected,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cerrar sesión", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            if (selectedTab == 0) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_madera),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Madera Poltand",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (isOnline) ColorApproved else ColorRejected)
                                    )
                                    Text(
                                        if (isOnline) "Conectado" else "Sin conexión",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isOnline) TextTertiary else ColorRejected
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BackgroundLight,
                        scrolledContainerColor = BackgroundLight
                    ),
                    actions = {
                        IconButton(onClick = { showLogoutDialog = true }) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Cerrar Sesión",
                                tint = TextTertiary
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceLight,
                tonalElevation = 0.dp,
                modifier = Modifier.shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                )
            ) {
                val tabs = listOf(
                    Triple(0, Icons.Default.Home, "Inicio"),
                    Triple(1, Icons.AutoMirrored.Filled.ListAlt, "Requerimientos"),
                    Triple(2, Icons.Default.Inventory, "Ingresos")
                )

                tabs.forEach { (index, icon, label) ->
                    val isSelected = selectedTab == index
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.1f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                icon,
                                contentDescription = label,
                                modifier = Modifier.graphicsLayer(scaleX = iconScale, scaleY = iconScale)
                            )
                        },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryWood,
                            selectedTextColor = PrimaryWood,
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary,
                            indicatorColor = AccentSoft
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(BackgroundLight)
        ) {

            // Offline banner
            AnimatedVisibility(
                visible = !isOnline,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    color = ColorRejected.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.WifiOff, contentDescription = null, tint = ColorRejected, modifier = Modifier.size(16.dp))
                        Text(
                            "Modo offline — los cambios se sincronizarán al reconectar",
                            style = MaterialTheme.typography.labelSmall,
                            color = ColorRejected
                        )
                    }
                }
            }

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { it / 4 } + fadeIn(tween(300))).togetherWith(
                            slideOutHorizontally { -it / 4 } + fadeOut(tween(200))
                        )
                    } else {
                        (slideInHorizontally { -it / 4 } + fadeIn(tween(300))).togetherWith(
                            slideOutHorizontally { it / 4 } + fadeOut(tween(200))
                        )
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SectionHeader(title = "Resumen Operativo")

        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardMetricCard(
                modifier = Modifier.weight(1f),
                title = "Total Reqs",
                value = total,
                icon = Icons.Default.Assessment,
                color = PrimaryWood,
                delay = 0
            )
            DashboardMetricCard(
                modifier = Modifier.weight(1f),
                title = "Pendientes",
                value = pendientes,
                icon = Icons.Default.HourglassBottom,
                color = ColorPending,
                delay = 80
            )
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardMetricCard(
                modifier = Modifier.weight(1f),
                title = "Parciales",
                value = parciales,
                icon = Icons.Default.Timelapse,
                color = StatusParcial,
                delay = 160
            )
            DashboardMetricCard(
                modifier = Modifier.weight(1f),
                title = "Completados",
                value = completados,
                icon = Icons.Default.CheckCircle,
                color = ColorApproved,
                delay = 240
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer branding
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_madera),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .graphicsLayer(alpha = 0.15f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Madera Poltand · ERP v1.5",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun DashboardMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: Int,
    icon: ImageVector,
    color: Color,
    delay: Int = 0
) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = 1200, delayMillis = delay, easing = FastOutSlowInEasing)
    )

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 30 }, animationSpec = tween(400))
    ) {
        GlassCard(
            modifier = modifier.height(130.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        title,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = color.copy(alpha = 0.1f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                icon,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Text(
                    animatedValue.toString(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
