package com.mars.madereraapp.ui.requerimientos

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
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

    LaunchedEffect(Unit) { viewModel.refresh() }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = { Text("Requerimientos", fontWeight = FontWeight.SemiBold, color = TextPrimary) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
                )
                // Barra de filtros
                Surface(
                    color = SurfaceDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                            modifier = Modifier.width(150.dp)
                        ) {
                            listOf("TODOS", "PENDIENTE", "PARCIAL", "COMPLETADO").forEach { est ->
                                DropdownMenuItem(
                                    text = { Text(est) },
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
                            modifier = Modifier.width(200.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text("TODAS") },
                                onClick = {
                                    viewModel.filtroMina.value = "TODAS"
                                    minaExpanded = false
                                }
                            )
                            minas.forEach { mina ->
                                DropdownMenuItem(
                                    text = { Text(mina.nombre) },
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
                                // Formato esperado "YYYY-MM-DD" o algo que matchee el string
                                // Simplificado a año-mes-dia
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
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar fecha", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
                Divider(color = BorderColor)
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
                        Text("Sin requerimientos", color = TextSecondary, fontSize = 16.sp)
                        Text("Modifica los filtros o desliza para actualizar", color = TextSecondary.copy(0.5f), fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
        color = if (isActive) PrimaryBlue.copy(alpha = 0.2f) else SurfaceVariant,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isActive) PrimaryBlue else BorderColor),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = if (isActive) PrimaryBlue else TextSecondary, modifier = Modifier.size(14.dp))
            }
            Text(
                text = text,
                color = if (isActive) PrimaryBlue else TextSecondary,
                fontSize = 13.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun RequerimientoCard(req: RequerimientoEntity, onClick: () -> Unit) {
    val isClickable = req.serverId != null
    Card(
        onClick = onClick,
        enabled = isClickable,
        modifier = Modifier.fillMaxWidth().then(
            if (!isClickable) Modifier.then(Modifier) else Modifier
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isClickable) SurfaceDark else SurfaceDark.copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, if (isClickable) BorderColor else BorderColor.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = req.codigo_req ?: "Pendiente sync",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
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
                    StatusChip(req.estado)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("📅  ${req.fecha}", color = TextSecondary, fontSize = 13.sp)
            Text("⛏  ${req.minaNombre}", color = TextSecondary, fontSize = 13.sp)
            req.supervisorNombre?.let {
                Text("👤  $it", color = TextSecondary, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (bg, fg) = when (status) {
        "COMPLETADO" -> Pair(StatusCompletado.copy(alpha = 0.15f), StatusCompletado)
        "PARCIAL"    -> Pair(StatusParcial.copy(alpha = 0.15f), StatusParcial)
        else         -> Pair(StatusPendiente.copy(alpha = 0.15f), StatusPendiente)
    }
    Surface(color = bg, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
