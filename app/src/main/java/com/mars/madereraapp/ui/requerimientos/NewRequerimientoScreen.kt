package com.mars.madereraapp.ui.requerimientos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.data.local.entities.RequerimientoDetalleEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequerimientoScreen(
    onNavigateBack: () -> Unit,
    viewModel: RequerimientoViewModel = hiltViewModel()
) {
    val minas by viewModel.minas.collectAsState()
    val articulos by viewModel.articulos.collectAsState()
    val proveedores by viewModel.proveedores.collectAsState()
    val supervisores by viewModel.supervisores.collectAsState()

    var selectedMina by remember { mutableStateOf<Int?>(null) }
    var selectedSupervisor by remember { mutableStateOf<Int?>(null) }
    val detalles = remember { mutableStateListOf<RequerimientoDetalleEntity>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Requerimiento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info
            Text("Información General", style = MaterialTheme.typography.titleMedium)
            
            // Note: Simple selector for demo purposes. In real app use a proper DropdownMenu.
            Text("Seleccione Mina (ID): ${selectedMina ?: "Ninguna"}")
            Button(onClick = { if (minas.isNotEmpty()) selectedMina = minas.first().id }) {
                Text("Simular Selección Mina")
            }

            Divider()

            Text("Artículos", style = MaterialTheme.typography.titleMedium)
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(detalles) { index, item ->
                    ListItem(
                        headlineContent = { Text(item.articuloNombre) },
                        supportingContent = { Text("Cant: ${item.cantidad} - Prov: ${item.proveedorNombre}") },
                        trailingContent = {
                            IconButton(onClick = { detalles.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    )
                }
            }

            Button(
                onClick = {
                    if (articulos.isNotEmpty() && proveedores.isNotEmpty()) {
                        val art = articulos.first()
                        val prov = proveedores.first()
                        detalles.add(
                            RequerimientoDetalleEntity(
                                localRequerimientoId = 0,
                                articulo_id = art.id,
                                articuloNombre = art.nombre,
                                proveedor_id = prov.id,
                                proveedorNombre = prov.nombre,
                                cantidad = 1.0,
                                precio_proveedor = art.precioProveedor,
                                precio_mina = art.precioMina
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar Artículo (Demo)")
            }

            val context = androidx.compose.ui.platform.LocalContext.current
            Button(
                onClick = {
                    if (selectedMina != null && detalles.isNotEmpty()) {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        viewModel.crearRequerimiento(
                            fecha = sdf.format(Date()),
                            minaId = selectedMina!!,
                            minaNombre = minas.find { it.id == selectedMina }?.nombre ?: "Mina",
                            supervisorId = selectedSupervisor,
                            supervisorNombre = supervisores.find { it.id == selectedSupervisor }?.nombre,
                            detalles = detalles.toList()
                        )
                        android.widget.Toast.makeText(context, "Requerimiento guardado. Sincronización en proceso.", android.widget.Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = selectedMina != null && detalles.isNotEmpty()
            ) {
                Text("Guardar Requerimiento")
            }
        }
    }
}
