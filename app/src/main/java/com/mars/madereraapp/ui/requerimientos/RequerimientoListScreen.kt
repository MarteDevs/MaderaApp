package com.mars.madereraapp.ui.requerimientos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.data.local.entities.RequerimientoEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequerimientoListScreen(
    onNavigateToCreate: () -> Unit,
    viewModel: RequerimientoViewModel = hiltViewModel()
) {
    val requerimientos by viewModel.requerimientos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Requerimientos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Requerimiento")
            }
        }
    ) { padding ->
        var isRefreshing by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
        val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing = true
                    viewModel.refresh()
                    isRefreshing = false
                }
            },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (requerimientos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay requerimientos registrados.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(requerimientos) { req ->
                        RequerimientoItem(req)
                    }
                }
            }
        }
    }
}

@Composable
fun RequerimientoItem(req: RequerimientoEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = req.codigo_req ?: "Pendiente Sync",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (req.isPendingSync) {
                    Icon(
                        Icons.Default.Sync,
                        contentDescription = "Sincronización pendiente",
                        tint = com.mars.madereraapp.ui.theme.ColorPending,
                        modifier = Modifier.size(16.dp)
                    )
                }
                StatusChip(req.estado)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Fecha: ${req.fecha}", style = MaterialTheme.typography.bodyMedium)
            Text("Mina: ${req.minaNombre}", style = MaterialTheme.typography.bodyMedium)
            req.supervisorNombre?.let {
                Text("Supervisor: $it", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when (status) {
        "COMPLETADO" -> Color(0xFF4CAF50)
        "PARCIAL" -> Color(0xFFFF9800)
        else -> Color(0xFF2196F3) // PENDIENTE
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            color = color,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
