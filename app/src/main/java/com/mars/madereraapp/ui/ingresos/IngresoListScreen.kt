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
import androidx.compose.ui.graphics.graphicsLayer
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
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Ingresos de Stock",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${ingresos.size} registros",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible,
                enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn()
            ) {
                FloatingActionButton(
                    onClick = onNavigateToCreate,
                    containerColor = PrimaryWood,
                    contentColor = TextOnPrimary,
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
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
            containerColor = BackgroundLight,
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(ingresos) { _, ing ->
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

@Composable
fun IngresoCard(ing: IngresoEntity, onClick: () -> Unit) {
    val isClickable = ing.serverId != null

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
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
                    text = ing.codigo_ingreso ?: "Pendiente sync",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                if (ing.isPendingSync) {
                    StatusBadge("Pendiente", ColorPending)
                } else {
                    StatusBadge("Sincronizado", ColorApproved)
                }
            }

            HorizontalDivider(color = DividerColor, modifier = Modifier.padding(vertical = 10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DetailRow(icon = Icons.Default.CalendarToday, text = ing.fecha)
                ing.viaje?.let { DetailRow(icon = Icons.Default.LocalShipping, text = "Viaje: $it") }
                ing.vale?.let { DetailRow(icon = Icons.Default.Receipt, text = "Vale: $it") }

                if (ing.total_proveedor > 0 || ing.total_mina > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "T. Prov: S/ ${"%.2f".format(ing.total_proveedor)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryWood,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "T. Mina: S/ ${"%.2f".format(ing.total_mina)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = ColorApproved,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
