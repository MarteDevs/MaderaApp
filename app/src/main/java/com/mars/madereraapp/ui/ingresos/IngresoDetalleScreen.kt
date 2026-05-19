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
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("DETALLE DE INGRESO", style = MaterialTheme.typography.titleLarge, color = PrimaryAmber)
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
                    CircularProgressIndicator(color = PrimaryAmber)
                }
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error!!, color = ColorRejected, style = MaterialTheme.typography.bodyMedium)
                }
                detalles.isEmpty() -> EmptyStateBox(
                    icon = Icons.Default.Inventory,
                    title = "Sin artículos en este ingreso",
                    subtitle = "Desliza para actualizar"
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    itemsIndexed(detalles) { index, item ->
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
                            IngresoDetalleCard(item)
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
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )

    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${animatedCantidad.toInt()}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = ColorApproved
                )
                Text("ENTREGADO", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }
    }
}
