package com.mars.madereraapp.ui.requerimientos

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.data.local.entities.RequerimientoEntity
import com.mars.madereraapp.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Calendar

import androidx.compose.ui.draw.alpha
import com.mars.madereraapp.ui.components.*
import com.mars.madereraapp.ui.theme.*

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

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(BackgroundDark)) {
                TopAppBar(
                    title = { Text("REQUERIMIENTOS", style = MaterialTheme.typography.titleLarge, color = PrimaryAmber) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
                )
                // Barra de filtros
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Filtro Estado
                    var estadoExpanded by remember { mutableStateOf(false) }
                    FilterButton(
                        text = if (filtroEstado == "TODOS") "Estado" else filtroEstado,
                        isActive = filtroEstado != "TODOS",
                        onClick = { estadoExpanded = true }
                    )
                    DropdownMenu(
                        expanded = estadoExpanded,
                        onDismissRequest = { estadoExpanded = false },
                        modifier = Modifier.width(150.dp).background(SurfaceContainer)
                    ) {
                        listOf("TODOS", "PENDIENTE", "PARCIAL", "COMPLETADO").forEach { est ->
                            DropdownMenuItem(
                                text = { Text(est, color = TextPrimary) },
                                onClick = {
                                    viewModel.filtroEstado.value = est
                                    estadoExpanded = false
                                }
                            )
                        }
                    }

                    // Filtro Mina
                    var minaExpanded by remember { mutableStateOf(false) }
                    FilterButton(
                        text = if (filtroMina == "TODAS") "Mina" else filtroMina,
                        isActive = filtroMina != "TODAS",
                        onClick = { minaExpanded = true }
                    )
                    DropdownMenu(
                        expanded = minaExpanded,
                        onDismissRequest = { minaExpanded = false },
                        modifier = Modifier.width(200.dp).background(SurfaceContainer)
                    ) {
                        DropdownMenuItem(
                            text = { Text("TODAS", color = TextPrimary) },
                            onClick = {
                                viewModel.filtroMina.value = "TODAS"
                                minaExpanded = false
                            }
                        )
                        minas.forEach { mina ->
                            DropdownMenuItem(
                                text = { Text(mina.nombre, color = TextPrimary) },
                                onClick = {
                                    viewModel.filtroMina.value = mina.nombre
                                    minaExpanded = false
                                }
                            )
                        }
                    }

                    // Filtro Fecha
                    val context = LocalContext.current
                    val calendar = Calendar.getInstance()
                    val datePickerDialog = DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val m = (month + 1).toString().padStart(2, '0')
                            val d = dayOfMonth.toString().padStart(2, '0')
                            viewModel.filtroFecha.value = "$year-$m-$d"
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )

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
        var isRefreshing by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    viewModel.refresh()
                    isRefreshing = false
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
    
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isClickable) Modifier.clickable { onClick() } else Modifier)
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

