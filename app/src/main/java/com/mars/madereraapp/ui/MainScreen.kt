package com.mars.madereraapp.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
                    0 -> DashboardTab(
                        viewModel = requerimientoViewModel,
                        onNavigateToRequerimientoDetalle = onNavigateToRequerimientoDetalle
                    )
                    1 -> RequerimientoListScreen(onNavigateToDetail = onNavigateToRequerimientoDetalle, viewModel = requerimientoViewModel)
                    2 -> IngresoListScreen(onNavigateToCreate = onNavigateToNuevoIngreso, onNavigateToDetail = onNavigateToIngresoDetalle)
                }
            }
        }
    }
}

@Composable
fun DashboardTab(
    viewModel: RequerimientoViewModel,
    onNavigateToRequerimientoDetalle: (Int) -> Unit
) {
    val scrollState = rememberScrollState()

    val totalReqs by viewModel.totalRequerimientos.collectAsState()
    val totalArticulos by viewModel.totalArticulos.collectAsState()
    val itemsPorEntregar by viewModel.itemsPorEntregar.collectAsState()
    val reqsConPendiente by viewModel.requerimientosConPendiente.collectAsState()

    val totalProvPendiente by viewModel.totalProveedorPendiente.collectAsState()
    val totalMinaPendiente by viewModel.totalMinaPendiente.collectAsState()

    val pendientes by viewModel.pendientes.collectAsState()
    val parciales by viewModel.parciales.collectAsState()
    val completados by viewModel.completados.collectAsState()
    val cancelados = remember(totalReqs, pendientes, parciales, completados) {
        (totalReqs - pendientes - parciales - completados).coerceAtLeast(0)
    }

    val ultimos by viewModel.ultimosRequerimientos.collectAsState()

    val today = remember { java.util.Date() }
    val dateFormatter = remember { java.text.SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", java.util.Locale("es", "PE")) }
    val fechaHoy = remember { dateFormatter.format(today).replaceFirstChar { it.uppercase() } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Welcome Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1E293B), Color(0xFF334155))
                    )
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "👋 ¡Bienvenido!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (reqsConPendiente > 0) {
                        "$reqsConPendiente requerimiento${if (reqsConPendiente != 1) "s" else ""} pendiente${if (reqsConPendiente != 1) "s" else ""} de entrega"
                    } else {
                        "✓ Todo al día — sin entregas pendientes"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (reqsConPendiente > 0) Color(0xFFFBBF24) else Color(0xFF34D399),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = fechaHoy,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // 2. Stats Grid (2 rows of 2 cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardMetricCard(
                modifier = Modifier.weight(1f),
                title = "Requerimientos",
                value = totalReqs,
                icon = Icons.AutoMirrored.Filled.ListAlt,
                color = Color(0xFF3B82F6),
                delay = 0
            )
            DashboardMetricCard(
                modifier = Modifier.weight(1f),
                title = "Pendientes",
                value = reqsConPendiente,
                icon = Icons.Default.HourglassBottom,
                color = Color(0xFFF59E0B),
                delay = 80
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardMetricCard(
                modifier = Modifier.weight(1f),
                title = "Artículos",
                value = totalArticulos,
                icon = Icons.Default.Inventory2,
                color = Color(0xFF10B981),
                delay = 160
            )
            DashboardMetricCard(
                modifier = Modifier.weight(1f),
                title = "Items x Entregar",
                value = itemsPorEntregar,
                icon = Icons.Default.LocalShipping,
                color = Color(0xFFA855F7),
                delay = 240
            )
        }

        // 3. Financial KPIs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardFinancialCard(
                modifier = Modifier.weight(1f),
                title = "Total Proveedor",
                amount = totalProvPendiente,
                icon = Icons.Default.Business,
                color = Color(0xFF2563EB),
                bgIconColor = Color(0xFFEFF6FF),
                labelSub = "Por entregar en reqs activos"
            )
            DashboardFinancialCard(
                modifier = Modifier.weight(1f),
                title = "Total Mina",
                amount = totalMinaPendiente,
                icon = Icons.Default.Diamond,
                color = Color(0xFF16A34A),
                bgIconColor = Color(0xFFF0FDF4),
                labelSub = "Por cobrar en reqs activos"
            )
        }

        // 4. State Distribution Progress Bar
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = null,
                        tint = PrimaryWood,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Distribución de Estados",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "$totalReqs total",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val statesList = listOf(
                    Triple("PENDIENTE", pendientes, Color(0xFFF59E0B)),
                    Triple("PARCIAL", parciales, Color(0xFFF97316)),
                    Triple("COMPLETADO", completados, Color(0xFF22C55E)),
                    Triple("CANCELADO", cancelados, Color(0xFFEF4444))
                )
                statesList.forEach { (label, count, color) ->
                    if (count > 0) {
                        val pct = if (totalReqs > 0) count.toFloat() / totalReqs.toFloat() else 0f
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                modifier = Modifier.width(85.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(BorderLight)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(pct)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(color)
                                )
                            }
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = color,
                                modifier = Modifier.width(20.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.End
                            )
                        }
                    }
                }
            }
        }

        // 5. Recent Requirements Table
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Assignment,
                    contentDescription = null,
                    tint = PrimaryWood,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Últimos Requerimientos",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (ultimos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay requerimientos registrados",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            } else {
                Column {
                    ultimos.forEachIndexed { index, req ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToRequerimientoDetalle(req.localId.toInt()) }
                                .padding(vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = req.codigo_req ?: "Borrador",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryWood
                                    )
                                    Text(
                                        text = "${req.fecha} · ${req.minaNombre}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextTertiary
                                    )
                                }
                                val badgeColor = when (req.estado) {
                                    "PENDIENTE" -> ColorPending
                                    "PARCIAL" -> StatusParcial
                                    "COMPLETADO" -> ColorApproved
                                    else -> ColorRejected
                                }
                                StatusBadge(text = req.estado, statusColor = badgeColor)
                            }
                        }
                        if (index < ultimos.lastIndex) {
                            HorizontalDivider(color = DividerColor, thickness = 1.dp)
                        }
                    }
                }
            }
        }

        // 6. Footer branding
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
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

@Composable
fun DashboardFinancialCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: Double,
    icon: ImageVector,
    color: Color,
    bgIconColor: Color,
    labelSub: String
) {
    val amountStr = remember(amount) {
        String.format(java.util.Locale("es", "PE"), "S/ %,.2f", amount)
    }

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
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = bgIconColor,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                    }
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = bgIconColor
                ) {
                    Text(
                        text = "ACTIVOS",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = amountStr,
                    style = MaterialTheme.typography.titleMedium,
                    color = color,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
                Text(
                    text = labelSub,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    fontSize = 8.sp
                )
            }
        }
    }
}
