package com.mars.madereraapp.ui.ingresos

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.data.local.entities.IngresoDetalleEntity
import com.mars.madereraapp.ui.components.*
import com.mars.madereraapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewIngresoScreen(
    onNavigateBack: () -> Unit,
    viewModel: IngresoViewModel = hiltViewModel()
) {
    val pendientes by viewModel.pendientes.collectAsState()

    var viaje by remember { mutableStateOf("") }
    var vale by remember { mutableStateOf("") }
    val seleccionados = remember { mutableStateMapOf<Int, Double>() }

    val itemsSeleccionados = seleccionados.count { it.value > 0 }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Nuevo Ingreso",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${pendientes.size} artículos pendientes",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            // Header fields
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SectionHeader(title = "Datos del Transporte")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassTextField(
                        value = viaje,
                        onValueChange = { viaje = it },
                        label = "Nro Viaje / Placa",
                        modifier = Modifier.weight(1f),
                        leadingIcon = {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(18.dp))
                        }
                    )
                    GlassTextField(
                        value = vale,
                        onValueChange = { vale = it },
                        label = "Nro Vale",
                        modifier = Modifier.weight(1f),
                        leadingIcon = {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(18.dp))
                        }
                    )
                }
            }

            HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))

            // Section header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(title = "Artículos Recibidos")
                if (itemsSeleccionados > 0) {
                    StatusBadge(
                        text = "$itemsSeleccionados seleccionados",
                        statusColor = ColorApproved
                    )
                }
            }

            // Items list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (pendientes.isEmpty()) {
                    item {
                        EmptyStateBox(
                            icon = Icons.Default.Inventory2,
                            title = "Sin artículos pendientes",
                            subtitle = "No hay artículos por entregar",
                            modifier = Modifier.height(200.dp)
                        )
                    }
                } else {
                    itemsIndexed(pendientes) { _, item ->
                        val cantidad = seleccionados[item.requerimiento_detalle_id] ?: 0.0
                        val isSelected = cantidad > 0

                        PendienteItemCard(
                            codigoReq = item.codigo_req,
                            articulo = item.articulo,
                            proveedor = item.proveedor,
                            faltante = item.faltante,
                            cantidadEntregada = cantidad,
                            isSelected = isSelected,
                            onCantidadChange = { newVal ->
                                seleccionados[item.requerimiento_detalle_id] = newVal
                            }
                        )
                    }
                }
            }

            // Footer button
            AnimatedVisibility(
                visible = itemsSeleccionados > 0,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                val context = androidx.compose.ui.platform.LocalContext.current
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = SurfaceLight,
                    shadowElevation = 8.dp
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        IndustrialButton(
                            onClick = {
                                val detalles = seleccionados.map { (id, cant) ->
                                    IngresoDetalleEntity(
                                        localIngresoId = 0,
                                        requerimiento_detalle_id = id,
                                        cantidad_entregada = cant
                                    )
                                }.filter { it.cantidad_entregada > 0 }

                                if (detalles.isNotEmpty()) {
                                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    viewModel.registrarIngreso(
                                        fecha = sdf.format(Date()),
                                        viaje = viaje.ifBlank { null },
                                        vale = vale.ifBlank { null },
                                        observacion = "Registro desde App Móvil",
                                        detalles = detalles
                                    )
                                    android.widget.Toast.makeText(
                                        context,
                                        "✓ Ingreso registrado. Sincronizando...",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                    onNavigateBack()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Confirmar Ingreso ($itemsSeleccionados items)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PendienteItemCard(
    codigoReq: String,
    articulo: String,
    proveedor: String,
    faltante: Double,
    cantidadEntregada: Double,
    isSelected: Boolean,
    onCantidadChange: (Double) -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryWood.copy(alpha = 0.5f) else DividerColor,
        animationSpec = tween(300)
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) AccentWarm else SurfaceLight,
        animationSpec = tween(300)
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        articulo,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isSelected) PrimaryWood else TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "$codigoReq · $proveedor",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Faltante",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                    Text(
                        "${faltante.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = ColorPending,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Entrega", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(
                        "${cantidadEntregada.toInt()} / ${faltante.toInt()}",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) PrimaryWood else TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Slider(
                    value = cantidadEntregada.toFloat(),
                    onValueChange = { onCantidadChange(it.toDouble()) },
                    valueRange = 0f..faltante.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryWood,
                        activeTrackColor = PrimaryWood,
                        inactiveTrackColor = DividerColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
