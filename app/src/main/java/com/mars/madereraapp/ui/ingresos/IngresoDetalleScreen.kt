package com.mars.madereraapp.ui.ingresos

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.data.remote.IngresoDetalleItem
import com.mars.madereraapp.ui.components.*
import com.mars.madereraapp.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresoDetalleScreen(
    ingresoId: Int,
    onNavigateBack: () -> Unit,
    viewModel: IngresoDetalleViewModel = hiltViewModel()
) {
    val detalles by viewModel.detalles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(ingresoId) { viewModel.load(ingresoId) }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Detalle de Ingreso",
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

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    viewModel.load(ingresoId)
                    isRefreshing = false
                }
            },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryWood, strokeWidth = 3.dp)
                }
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error!!, color = ColorRejected, style = MaterialTheme.typography.bodyMedium)
                }
                detalles.isEmpty() -> EmptyStateBox(
                    icon = Icons.Default.Inventory,
                    title = "Sin artículos en este ingreso",
                    subtitle = "Desliza para actualizar"
                )
                else -> {
                    val totalProv = detalles.sumOf { it.cantidad_entregada * it.precio_proveedor }
                    val totalMina = detalles.sumOf { it.cantidad_entregada * it.precio_mina }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(detalles) { _, item ->
                            IngresoDetalleCard(item)
                        }

                        if (detalles.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(), 
                                    shape = RoundedCornerShape(16.dp),
                                    color = SurfaceContainer
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                        Text(
                                            "COSTO TOTAL DEL VIAJE",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextTertiary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("Proveedor", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                                Text(
                                                    "S/ ${"%.2f".format(totalProv)}",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PrimaryWood
                                                )
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Mina", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                                Text(
                                                    "S/ ${"%.2f".format(totalMina)}",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ColorApproved
                                                )
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
    }
}

@Composable
private fun IngresoDetalleCard(item: IngresoDetalleItem) {
    val animatedCantidad by animateFloatAsState(
        targetValue = item.cantidad_entregada.toFloat(),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )

    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            item.articulo,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (item.isExtra) {
                            StatusBadge("Extra", ColorApproved)
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        item.proveedor,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${animatedCantidad.toInt()}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ColorApproved
                    )
                    Text("Entregado", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                }
            }

            if (item.precio_proveedor > 0 || item.precio_mina > 0) {
                HorizontalDivider(color = DividerColor, modifier = Modifier.padding(vertical = 10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("T. Prov", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                        Text(
                            "S/ ${"%.2f".format(item.cantidad_entregada * item.precio_proveedor)}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryWood
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("T. Mina", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                        Text(
                            "S/ ${"%.2f".format(item.cantidad_entregada * item.precio_mina)}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ColorApproved
                        )
                    }
                }
            }
        }
    }
}
