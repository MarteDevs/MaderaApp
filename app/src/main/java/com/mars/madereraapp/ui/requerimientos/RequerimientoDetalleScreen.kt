package com.mars.madereraapp.ui.requerimientos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.data.remote.RequerimientoDetalleItem
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
                title = { Text("Detalle del Requerimiento", fontWeight = FontWeight.SemiBold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
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
                    viewModel.load(requerimientoId)
                    isRefreshing = false
                }
            },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error!!, color = ColorError, fontSize = 14.sp)
                }
                detalles.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sin artículos encontrados", color = TextSecondary)
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(detalles) { item -> DetalleArticuloCard(item) }
                }
            }
        }
    }
}

@Composable
private fun DetalleArticuloCard(item: RequerimientoDetalleItem) {
    val progreso = if (item.pedido > 0) (item.entregado / item.pedido).toFloat().coerceIn(0f, 1f) else 0f
    val colorBarra = when {
        progreso >= 1f   -> StatusCompletado
        progreso > 0f    -> StatusParcial
        else             -> StatusPendiente
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.articulo, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp)
            Text("Proveedor: ${item.proveedor}", color = TextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progreso visual
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = colorBarra,
                trackColor = SurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricChip("Pedido", item.pedido.toInt().toString(), TextSecondary)
                MetricChip("Entregado", item.entregado.toInt().toString(), StatusCompletado)
                MetricChip("Faltante", item.faltante.toInt().toString(), if (item.faltante > 0) StatusParcial else StatusCompletado)
            }
        }
    }
}

@Composable
private fun MetricChip(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = valueColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = TextSecondary, fontSize = 11.sp)
    }
}
