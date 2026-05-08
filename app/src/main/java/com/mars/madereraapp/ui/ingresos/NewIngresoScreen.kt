package com.mars.madereraapp.ui.ingresos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.data.local.entities.IngresoDetalleEntity
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Ingreso") },
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
            TextField(
                value = viaje,
                onValueChange = { viaje = it },
                label = { Text("Nro Viaje / Placa") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = vale,
                onValueChange = { vale = it },
                label = { Text("Nro Vale") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Seleccione artículos recibidos:", style = MaterialTheme.typography.titleMedium)
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(pendientes) { item ->
                    val cantidad = seleccionados[item.requerimiento_detalle_id] ?: 0.0
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (cantidad > 0) MaterialTheme.colorScheme.secondaryContainer 
                                           else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("${item.codigo_req} - ${item.articulo}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            Text("Pendiente: ${item.faltante} ${item.proveedor}")
                            
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Text("Cantidad: ")
                                Slider(
                                    value = cantidad.toFloat(),
                                    onValueChange = { seleccionados[item.requerimiento_detalle_id] = it.toDouble() },
                                    valueRange = 0f..item.faltante.toFloat(),
                                    modifier = Modifier.weight(1f)
                                )
                                Text("%.1f".format(cantidad))
                            }
                        }
                    }
                }
            }

            val context = androidx.compose.ui.platform.LocalContext.current
            Button(
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
                            viaje = viaje,
                            vale = vale,
                            observacion = "Registro desde App Móvil",
                            detalles = detalles
                        )
                        android.widget.Toast.makeText(context, "Ingreso registrado localmente. Sincronizando...", android.widget.Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = seleccionados.values.any { it > 0 }
            ) {
                Text("Confirmar Ingreso")
            }
        }
    }
}
