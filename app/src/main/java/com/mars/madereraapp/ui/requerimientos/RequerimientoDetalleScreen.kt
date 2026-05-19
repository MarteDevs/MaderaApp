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
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("DETALLE DE REQUERIMIENTO", style = MaterialTheme.typography.titleLarge, color = PrimaryAmber)
                        if (detalles.isNotEmpty()) {
                            Text(
                                "${detalles.size} artículos",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = PrimaryAmber)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { padding ->
        var isRefreshing by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        AnimatedContent(
            targetState = Triple(isLoading, error, detalles),
            transitionSpec = {
                fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
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
                        CircularProgressIndicator(color = PrimaryAmber)
                    }
                    err != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(err ?: "Error desconocido", color = ColorRejected, style = MaterialTheme.typography.bodyMedium)
                    }
                    itemsList.isEmpty() -> EmptyStateBox(
                        icon = Icons.AutoMirrored.Filled.ListAlt,
                        title = "Sin artículos encontrados",
                        subtitle = "Desliza para actualizar"
                    )
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        itemsIndexed(itemsList) { index, item ->
                            val delay = (index * 60).coerceAtMost(500)
                            var visible by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(delay.toLong())
                                visible = true
                            }
                            AnimatedVisibility(
                                visible = visible,
                                enter = fadeIn(animationSpec = tween(400)) +
                                        slideInVertically(initialOffsetY = { it / 3 }, animationSpec = tween(400))
                            ) {
                                DetalleArticuloCard(item)
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
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "ProgressBarAnimation"
    )

    val colorBarra = when {
        animatedProgreso >= 1f   -> ColorApproved
        animatedProgreso > 0f    -> ColorPending
        else                     -> ColorPending
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
                        item.articulo.uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        color = PrimaryAmber,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "PROVEEDOR: ${item.proveedor}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
                // Percentage badge
                StatusBadge(
                    text = "${(animatedProgreso * 100).toInt()}%",
                    statusColor = colorBarra
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de progreso visual premium animada
            Box(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { animatedProgreso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = colorBarra,
                    trackColor = GlassWhite
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricChip("PEDIDO", item.pedido.toInt().toString(), TextSecondary)
                MetricChip("ENTREGADO", item.entregado.toInt().toString(), ColorApproved)

                val faltante = item.faltante.toInt()
                if (faltante < 0) {
                    MetricChip("EXTRA", (faltante * -1).toString(), ColorApproved)
                } else {
                    MetricChip("FALTANTE", faltante.toString(), if (faltante > 0) ColorPending else ColorApproved)
                }
            }
        }
    }
}

@Composable
private fun MetricChip(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = valueColor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}
