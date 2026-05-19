package com.mars.madereraapp.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
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
            kotlinx.coroutines.delay(5000) // Check every 5s
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = SurfaceContainer,
            titleContentColor = PrimaryAmber,
            textContentColor = TextSecondary,
            title = { Text("CERRAR SESIÓN", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas salir del sistema?") },
            confirmButton = {
                IndustrialButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("CONFIRMAR", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("CANCELAR", color = TextSecondary)
                }
            }
        )
    }

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
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("MADERA POLTAND", style = MaterialTheme.typography.titleLarge, color = PrimaryAmber)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Online/offline indicator dot
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(if (isOnline) ColorApproved else ColorRejected)
                                    )
                                    Text(
                                        if (isOnline) "CONECTADO" else "SIN CONEXIÓN",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isOnline) TextSecondary else ColorRejected
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark),
                    actions = {
                        IconButton(onClick = { showLogoutDialog = true }) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar Sesión", tint = TextSecondary)
                        }
                    }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .border(1.dp, GlassWhite, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                val tabs = listOf(
                    Triple(0, Icons.Default.Home, "Inicio"),
                    Triple(1, Icons.AutoMirrored.Filled.ListAlt, "Reqs"),
                    Triple(2, Icons.Default.Inventory, "Ingresos")
                )

                tabs.forEach { (index, icon, label) ->
                    val isSelected = selectedTab == index
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1f,
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
                            selectedIconColor = PrimaryAmber,
                            selectedTextColor = PrimaryAmber,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PrimaryAmber.copy(alpha = 0.12f)
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
                    colors = listOf(Color(0xFF0A0A0A), BackgroundDark)
                )
            )) {

            // Offline banner
            AnimatedVisibility(
                visible = !isOnline,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    color = ColorRejected.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.WifiOff, contentDescription = null, tint = ColorRejected, modifier = Modifier.size(16.dp))
                        Text("Modo offline — los cambios se sincronizarán al reconectar", style = MaterialTheme.typography.labelSmall, color = ColorRejected)
                    }
                }
            }

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { it / 3 } + fadeIn(tween(250))).togetherWith(
                            slideOutHorizontally { -it / 3 } + fadeOut(tween(200))
                        )
                    } else {
                        (slideInHorizontally { -it / 3 } + fadeIn(tween(250))).togetherWith(
                            slideOutHorizontally { it / 3 } + fadeOut(tween(200))
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
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(title = "Resumen Operativo")

        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DashboardMetricCard(
                modifier = Modifier.weight(1f),
                title = "Total Reqs",
                value = total,
                icon = Icons.Default.Assessment,
                color = PrimaryAmber,
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
            horizontalArrangement = Arrangement.spacedBy(14.dp)
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
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .graphicsLayer(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "INDUSTRIAL ERP • PREMIUM",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary.copy(alpha = 0.15f)
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
        animationSpec = tween(durationMillis = 1500, delayMillis = delay, easing = FastOutSlowInEasing)
    )

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(500))
    ) {
        GlassCard(
            modifier = modifier.height(140.dp)
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
                        title.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = color.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
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
