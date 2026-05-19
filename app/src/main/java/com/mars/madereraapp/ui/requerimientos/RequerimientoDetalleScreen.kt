package com.mars.madereraapp.ui.requerimientos

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.data.remote.RequerimientoDetalleItem
import com.mars.madereraapp.ui.components.*
import com.mars.madereraapp.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequerimientoDetalleScreen(
    requerimientoId: Int,
    onNavigateBack: () -> Unit,
    viewModel: RequerimientoDetalleViewModel = hiltViewModel()
) {
    val detalles by viewModel.detalles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(requerimientoId) { viewModel.load(requerimientoId) }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Detalle de Requerimiento",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        if (detalles.isNotEmpty()) {
                            Text(
                                "${detalles.size} artículos",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        }
    ) { padding ->
        var isRefreshing by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        AnimatedContent(
            targetState = Triple(isLoading, error, detalles),
            transitionSpec = {
                fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(350))
            },
            label = "StateTransition",
            modifier = Modifier.padding(padding)
        ) { (loading, err, itemsList) ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    scope.launch {
                        isRefreshing = true
                        viewModel.load(requerimientoId)
                        isRefreshing = false
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryWood, strokeWidth = 3.dp)
                    }
                    err != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(err ?: "Error desconocido", color = ColorRejected, style = MaterialTheme.typography.bodyMedium)
                    }
                    itemsList.isEmpty() -> EmptyStateBox(
                        icon = Icons.AutoMirrored.Filled.ListAlt,
                        title = "Sin artículos encontrados",
                        subtitle = "Desliza para actualizar"
                    )
                    else -> {
                        val showCierreBtn = itemsList.any { it.faltante > 0 }
                        var showConfirmDialog by remember { mutableStateOf(false) }
                        val isClosing by viewModel.isLoading.collectAsState()
                        val cierreSuccess by viewModel.cierreSuccess.collectAsState()

                        LaunchedEffect(cierreSuccess) {
                            if (cierreSuccess) {
                                onNavigateBack()
                            }
                        }

                        if (showConfirmDialog) {
                            AlertDialog(
                                onDismissRequest = { showConfirmDialog = false },
                                title = { Text("¿Dar por Completado?") },
                                text = { Text("Los ítems que faltan entregar se cancelarán. Esta acción es definitiva.") },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            showConfirmDialog = false
                                            viewModel.forzarCierre(requerimientoId)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ColorApproved)
                                    ) {
                                        Text("Completar")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showConfirmDialog = false }) { Text("Cancelar") }
                                }
                            )
                        }

                        Box(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = if(showCierreBtn) 100.dp else 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                itemsIndexed(itemsList) { _, item ->
                                    DetalleArticuloCard(item)
                                }
                            }

                            if (showCierreBtn) {
                                Button(
                                    onClick = { showConfirmDialog = true },
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorApproved),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isClosing
                                ) {
                                    if (isClosing) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Dar por Completado", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetalleArticuloCard(item: RequerimientoDetalleItem) {
    val targetProgreso = if (item.pedido > 0) (item.entregado / item.pedido).toFloat().coerceIn(0f, 1f) else 0f

    val animatedProgreso by animateFloatAsState(
        targetValue = targetProgreso,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "ProgressBarAnimation"
    )

    val colorBarra = when {
        animatedProgreso >= 1f   -> ColorApproved
        animatedProgreso > 0f    -> ColorPending
        else                     -> DividerColor
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.articulo,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        item.proveedor,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
                // Percentage badge
                StatusBadge(
                    text = "${(animatedProgreso * 100).toInt()}%",
                    statusColor = colorBarra
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgreso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = colorBarra,
                trackColor = DividerColor
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricChip("Pedido", item.pedido.toInt().toString(), TextSecondary)
                MetricChip("Entregado", item.entregado.toInt().toString(), ColorApproved)

                val faltante = item.faltante.toInt()
                if (faltante < 0) {
                    MetricChip("Extra", (faltante * -1).toString(), ColorApproved)
                } else {
                    MetricChip("Faltante", faltante.toString(), if (faltante > 0) ColorPending else ColorApproved)
                }
            }
        }
    }
}

@Composable
private fun MetricChip(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = valueColor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
    }
}
