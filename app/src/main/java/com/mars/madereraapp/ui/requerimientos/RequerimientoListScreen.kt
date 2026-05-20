package com.mars.madereraapp.ui.requerimientos

import android.app.DatePickerDialog
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.data.local.entities.RequerimientoEntity
import com.mars.madereraapp.ui.components.*
import com.mars.madereraapp.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequerimientoListScreen(
    onNavigateToDetail: (Int) -> Unit = {},
    viewModel: RequerimientoViewModel = hiltViewModel()
) {
    val requerimientos by viewModel.requerimientosFiltrados.collectAsState()
    val minas by viewModel.minas.collectAsState()

    val filtroEstado by viewModel.filtroEstado.collectAsState()
    val filtroMina by viewModel.filtroMina.collectAsState()
    val filtroFecha by viewModel.filtroFecha.collectAsState()

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            val dateStr = String.format("%04d-%02d-%02d", year, month + 1, day)
            viewModel.filtroFecha.value = dateStr
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(BackgroundLight)) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "Requerimientos",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${requerimientos.size} registros",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChipButton(
                        text = "Todos",
                        isActive = filtroEstado == "TODOS" && filtroMina == "TODAS" && filtroFecha == null,
                        onClick = {
                            viewModel.filtroEstado.value = "TODOS"
                            viewModel.filtroMina.value = "TODAS"
                            viewModel.filtroFecha.value = null
                        }
                    )

                    FilterChipButton(
                        text = "Pendientes",
                        isActive = filtroEstado == "PENDIENTE",
                        onClick = { viewModel.filtroEstado.value = if (filtroEstado == "PENDIENTE") "TODOS" else "PENDIENTE" }
                    )

                    minas.forEach { mina ->
                        FilterChipButton(
                            text = mina.nombre,
                            isActive = filtroMina == mina.nombre,
                            onClick = { viewModel.filtroMina.value = if (filtroMina == mina.nombre) "TODAS" else mina.nombre }
                        )
                    }

                    FilterChipButton(
                        text = filtroFecha ?: "Fecha",
                        isActive = filtroFecha != null,
                        icon = Icons.Default.CalendarToday,
                        onClick = { datePickerDialog.show() }
                    )

                    if (filtroFecha != null) {
                        IconButton(
                            onClick = { viewModel.filtroFecha.value = null },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar fecha", tint = PrimaryWood, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                HorizontalDivider(color = DividerColor)
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
            val searchQuery by viewModel.searchQuery.collectAsState()

            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                    placeholder = { Text("Buscar código, mina o supervisor...", color = TextTertiary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryWood) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryWood,
                        unfocusedBorderColor = DividerColor,
                        focusedContainerColor = SurfaceContainer,
                        unfocusedContainerColor = SurfaceContainer
                    )
                )

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        scope.launch {
                            isRefreshing = true
                            viewModel.refresh()
                            isRefreshing = false
                            snackbarHostState.showSnackbar(
                                message = "✓ Base de datos actualizada",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                if (requerimientos.isEmpty()) {
                    EmptyStateBox(
                        icon = Icons.AutoMirrored.Filled.ListAlt,
                        title = "Sin requerimientos",
                        subtitle = "Desliza hacia abajo para actualizar"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(requerimientos) { _, req ->
                            RequerimientoCard(req, onClick = {
                                val sid = req.serverId
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
private fun FilterChipButton(
    text: String,
    isActive: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.03f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Surface(
        color = if (isActive) PrimaryWood.copy(alpha = 0.1f) else SurfaceContainer,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (isActive) PrimaryWood.copy(alpha = 0.4f) else DividerColor),
        onClick = onClick,
        modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = if (isActive) PrimaryWood else TextTertiary, modifier = Modifier.size(14.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = if (isActive) PrimaryWood else TextSecondary,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun RequerimientoCard(req: RequerimientoEntity, onClick: () -> Unit) {
    val isClickable = req.serverId != null

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
            .then(if (!isClickable) Modifier.alpha(0.5f) else Modifier)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = req.codigo_req ?: "Sin código",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (req.isPendingSync) {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = "Pendiente",
                            tint = ColorPending,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    val statusColor = when (req.estado) {
                        "COMPLETADO" -> ColorApproved
                        "PARCIAL"    -> ColorPending
                        else         -> ColorPending
                    }
                    StatusBadge(req.estado, statusColor)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryWood, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = req.fecha, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Terrain, contentDescription = null, tint = PrimaryWood, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Mina: ${req.minaNombre}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }

                req.supervisorNombre?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryWood, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Supervisor: $it", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                }

                if (req.total_proveedor > 0 || req.total_mina > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "T. Prov: S/ ${"%.2f".format(req.total_proveedor)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryWood, // Blue accent
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "T. Mina: S/ ${"%.2f".format(req.total_mina)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = ColorApproved, // Green accent
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(15.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}
