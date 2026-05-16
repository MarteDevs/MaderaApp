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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        containerColor = BackgroundDark,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(BackgroundDark)) {
                TopAppBar(
                    title = { Text("REQUERIMIENTOS", style = MaterialTheme.typography.titleLarge, color = PrimaryAmber) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterButton(
                        text = "Todos",
                        isActive = filtroEstado == "TODOS" && filtroMina == "TODAS" && filtroFecha == null,
                        onClick = {
                            viewModel.filtroEstado.value = "TODOS"
                            viewModel.filtroMina.value = "TODAS"
                            viewModel.filtroFecha.value = null
                        }
                    )
                    
                    FilterButton(
                        text = "Pendientes",
                        isActive = filtroEstado == "PENDIENTE",
                        onClick = { viewModel.filtroEstado.value = if (filtroEstado == "PENDIENTE") "TODOS" else "PENDIENTE" }
                    )

                    minas.forEach { mina ->
                        FilterButton(
                            text = mina.nombre,
                            isActive = filtroMina == mina.nombre,
                            onClick = { viewModel.filtroMina.value = if (filtroMina == mina.nombre) "TODAS" else mina.nombre }
                        )
                    }

                    FilterButton(
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
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar fecha", tint = PrimaryAmber, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Divider(color = GlassWhite, modifier = Modifier.padding(top = 4.dp))
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
                            message = "✓ Base de datos actualizada",
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                if (requerimientos.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SIN REQUERIMIENTOS", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                            Text("DESLIZA PARA ACTUALIZAR", style = MaterialTheme.typography.labelSmall, color = TextSecondary.copy(0.4f))
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(requerimientos) { req ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically { it / 2 }
                            ) {
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
private fun FilterButton(
    text: String,
    isActive: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit
) {
    Surface(
        color = if (isActive) PrimaryAmber.copy(alpha = 0.2f) else SurfaceContainer,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isActive) PrimaryAmber else GlassWhite),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = if (isActive) PrimaryAmber else TextSecondary, modifier = Modifier.size(16.dp))
            }
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = if (isActive) PrimaryAmber else TextSecondary
            )
        }
    }
}

@Composable
fun RequerimientoCard(req: RequerimientoEntity, onClick: () -> Unit) {
    val isClickable = req.serverId != null
    
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.97f else 1f)

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .then(if (isClickable) Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ) else Modifier)
            .then(if (!isClickable) Modifier.alpha(0.6f) else Modifier)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = req.codigo_req ?: "SIN CÓDIGO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryAmber
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (req.isPendingSync) {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = "Pendiente",
                            tint = ColorPending,
                            modifier = Modifier.size(16.dp)
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
            
            Divider(color = GlassWhite, modifier = Modifier.padding(vertical = 12.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoRow(icon = "📅", text = req.fecha)
                InfoRow(icon = "⛏", text = req.minaNombre)
                req.supervisorNombre?.let {
                    InfoRow(icon = "👤", text = it)
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, modifier = Modifier.width(24.dp), fontSize = 14.sp)
        Text(text, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}
