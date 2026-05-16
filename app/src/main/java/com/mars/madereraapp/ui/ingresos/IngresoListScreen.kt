package com.mars.madereraapp.ui.ingresos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sync
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
import com.mars.madereraapp.data.local.entities.IngresoEntity
import com.mars.madereraapp.ui.theme.*
import kotlinx.coroutines.launch

import com.mars.madereraapp.ui.components.*
import com.mars.madereraapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresoListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Int) -> Unit = {},
    viewModel: IngresoViewModel = hiltViewModel()
) {
    val ingresos by viewModel.ingresos.collectAsState()

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("INGRESOS DE STOCK", style = MaterialTheme.typography.titleLarge, color = PrimaryAmber) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = PrimaryAmber,
                contentColor = TextOnPrimary,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar Ingreso")
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
            if (ingresos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SIN INGRESOS REGISTRADOS", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                        Text("DESLIZA PARA ACTUALIZAR", style = MaterialTheme.typography.labelSmall, color = TextSecondary.copy(0.4f))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(ingresos) { ing ->
                        IngresoCard(ing, onClick = {
                            val sid = ing.serverId
                            if (sid != null) onNavigateToDetail(sid)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun IngresoCard(ing: IngresoEntity, onClick: () -> Unit) {
    val isClickable = ing.serverId != null
    
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isClickable) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ing.codigo_ingreso ?: "PENDIENTE SYNC",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryAmber
                )
                if (ing.isPendingSync) {
                    StatusBadge("PENDIENTE", ColorPending)
                } else {
                    StatusBadge("SINCRONIZADO", ColorApproved)
                }
            }
            
            Divider(color = GlassWhite, modifier = Modifier.padding(vertical = 12.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoRow(icon = "📅", text = ing.fecha)
                ing.viaje?.let { InfoRow(icon = "🚛", text = "Viaje: $it") }
                ing.vale?.let { InfoRow(icon = "🧾", text = "Vale: $it") }
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

