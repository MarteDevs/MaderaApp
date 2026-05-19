package com.mars.madereraapp.ui.ingresos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.data.local.entities.IngresoEntity
import com.mars.madereraapp.ui.components.*
import com.mars.madereraapp.ui.requerimientos.DetailRow
import com.mars.madereraapp.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresoListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Int) -> Unit = {},
    viewModel: IngresoViewModel = hiltViewModel()
) {
    val ingresos by viewModel.ingresos.collectAsState()

    // FAB animation
    var fabVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(400)
        fabVisible = true
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("INGRESOS DE STOCK", style = MaterialTheme.typography.titleLarge, color = PrimaryAmber)
                        Text(
                            "${ingresos.size} registros",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible,
                enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn()
            ) {
                FloatingActionButton(
                    onClick = onNavigateToCreate,
                    containerColor = PrimaryAmber,
                    contentColor = TextOnPrimary,
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Registrar Ingreso")
                }
            }
        }
    ) { padding ->
        val snackbarHostState = remember { SnackbarHostState() }
        var isRefreshing by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    scope.launch {
                        isRefreshing = true
                        viewModel.refresh()
                        isRefreshing = false
                        snackbarHostState.showSnackbar(
                            message = "✓ Datos de ingresos actualizados",
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                if (ingresos.isEmpty()) {
                    EmptyStateBox(
                        icon = Icons.Default.Inventory,
                        title = "Sin ingresos registrados",
                        subtitle = "Desliza hacia abajo para actualizar"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        itemsIndexed(ingresos) { index, ing ->
                            val delay = (index * 40).coerceAtMost(400)
                            var visible by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(delay.toLong())
                                visible = true
                            }
                            AnimatedVisibility(
                                visible = visible,
                                enter = fadeIn(animationSpec = tween(350)) +
                                        slideInVertically(initialOffsetY = { it / 4 }, animationSpec = tween(350))
                            ) {
                                IngresoCard(ing, onClick = {
                                    val sid = ing.serverId
                                    if (sid != null) onNavigateToDetail(sid)
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IngresoCard(ing: IngresoEntity, onClick: () -> Unit) {
    val isClickable = ing.serverId != null

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .then(if (isClickable) Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ) else Modifier)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ing.codigo_ingreso ?: "PENDIENTE SYNC",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryAmber
                )
                if (ing.isPendingSync) {
                    StatusBadge("PENDIENTE", ColorPending)
                } else {
                    StatusBadge("SINCRONIZADO", ColorApproved)
                }
            }

            HorizontalDivider(color = GlassWhite, modifier = Modifier.padding(vertical = 12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailRow(icon = Icons.Default.CalendarToday, text = ing.fecha)
                ing.viaje?.let { DetailRow(icon = Icons.Default.LocalShipping, text = "Viaje: $it") }
                ing.vale?.let { DetailRow(icon = Icons.Default.Receipt, text = "Vale: $it") }
            }
        }
    }
}
