package com.mars.madereraapp.ui.analisis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalisisScreen(
    viewModel: AnalisisViewModel = hiltViewModel()
) {
    val filtroMes by viewModel.filtroMes.collectAsState()
    val filtroAnio by viewModel.filtroAnio.collectAsState()
    val filtroDestino by viewModel.filtroDestino.collectAsState()
    val aniosDisponibles by viewModel.aniosDisponibles.collectAsState()
    
    val totalProvReq by viewModel.totalProveedorReq.collectAsState()
    val totalMinaReq by viewModel.totalMinaReq.collectAsState()
    val totalProvIng by viewModel.totalProveedorIngreso.collectAsState()
    val totalMinaIng by viewModel.totalMinaIngreso.collectAsState()

    var expandedMes by remember { mutableStateOf(false) }
    var expandedAnio by remember { mutableStateOf(false) }
    var expandedDestino by remember { mutableStateOf(false) }

    val mesesOpciones = listOf(
        "" to "Todos", "01" to "Enero", "02" to "Febrero", "03" to "Marzo",
        "04" to "Abril", "05" to "Mayo", "06" to "Junio", "07" to "Julio",
        "08" to "Agosto", "09" to "Septiembre", "10" to "Octubre",
        "11" to "Noviembre", "12" to "Diciembre"
    )

    val destinoOpciones = listOf("TODOS", "DEPOSITO", "DIRECTO")
    val destinoLabels = mapOf("TODOS" to "Todos", "DEPOSITO" to "Depósito", "DIRECTO" to "Directo")

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Business Analytics",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Filtros Globales
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expandedMes,
                    onExpandedChange = { expandedMes = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = mesesOpciones.find { it.first == filtroMes }?.second ?: "Todos",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mes", style = MaterialTheme.typography.labelSmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMes) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                            focusedBorderColor = PrimaryWood,
                            unfocusedBorderColor = DividerColor,
                            focusedContainerColor = SurfaceContainer,
                            unfocusedContainerColor = SurfaceContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMes,
                        onDismissRequest = { expandedMes = false }
                    ) {
                        mesesOpciones.forEach { (clave, nombre) ->
                            DropdownMenuItem(
                                text = { Text(nombre) },
                                onClick = {
                                    viewModel.updateFiltroMes(clave)
                                    expandedMes = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expandedAnio,
                    onExpandedChange = { expandedAnio = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = filtroAnio.ifBlank { "Todos" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Año", style = MaterialTheme.typography.labelSmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAnio) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                            focusedBorderColor = PrimaryWood,
                            unfocusedBorderColor = DividerColor,
                            focusedContainerColor = SurfaceContainer,
                            unfocusedContainerColor = SurfaceContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedAnio,
                        onDismissRequest = { expandedAnio = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos") },
                            onClick = {
                                viewModel.updateFiltroAnio("")
                                expandedAnio = false
                            }
                        )
                        aniosDisponibles.forEach { a ->
                            DropdownMenuItem(
                                text = { Text(a) },
                                onClick = {
                                    viewModel.updateFiltroAnio(a)
                                    expandedAnio = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expandedDestino,
                    onExpandedChange = { expandedDestino = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = destinoLabels[filtroDestino] ?: "Todos",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Destino", style = MaterialTheme.typography.labelSmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDestino) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                            focusedBorderColor = PrimaryWood,
                            unfocusedBorderColor = DividerColor,
                            focusedContainerColor = SurfaceContainer,
                            unfocusedContainerColor = SurfaceContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDestino,
                        onDismissRequest = { expandedDestino = false }
                    ) {
                        destinoOpciones.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(destinoLabels[opcion] ?: opcion) },
                                onClick = {
                                    viewModel.updateFiltroDestino(opcion)
                                    expandedDestino = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Análisis de Requerimientos
            Text(
                text = "Análisis de Requerimientos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f).height(120.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp)).padding(6.dp)) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color(0xFF1E40AF), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PROVEEDOR", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(String.format("S/ %.2f", totalProvReq), style = MaterialTheme.typography.titleLarge, color = Color(0xFF1E40AF), fontWeight = FontWeight.ExtraBold)
                    }
                }
                
                Card(
                    modifier = Modifier.weight(1f).height(120.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.background(Color(0xFFF0FDF4), RoundedCornerShape(8.dp)).padding(6.dp)) {
                                Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("MINA", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(String.format("S/ %.2f", totalMinaReq), style = MaterialTheme.typography.titleLarge, color = Color(0xFF166534), fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            // Rentabilidad Requerimientos
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp)).padding(6.dp)) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DIFERENCIA (MINA - PROV)", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }
                    Text(String.format("S/ %.2f", totalMinaReq - totalProvReq), style = MaterialTheme.typography.titleMedium, color = if (totalMinaReq - totalProvReq >= 0) ColorApproved else ColorRejected, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Análisis de Ingresos
            Text(
                text = "Análisis de Ingresos (Vales)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f).height(120.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.background(Color(0xFFFDF4FF), RoundedCornerShape(8.dp)).padding(6.dp)) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color(0xFF701A75), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PROVEEDOR", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(String.format("S/ %.2f", totalProvIng), style = MaterialTheme.typography.titleLarge, color = Color(0xFF701A75), fontWeight = FontWeight.ExtraBold)
                    }
                }
                
                Card(
                    modifier = Modifier.weight(1f).height(120.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.background(Color(0xFFFFFBEB), RoundedCornerShape(8.dp)).padding(6.dp)) {
                                Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF78350F), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("MINA", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(String.format("S/ %.2f", totalMinaIng), style = MaterialTheme.typography.titleLarge, color = Color(0xFF78350F), fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            
            // Rentabilidad Ingresos
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp)).padding(6.dp)) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DIFERENCIA (MINA - PROV)", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }
                    Text(String.format("S/ %.2f", totalMinaIng - totalProvIng), style = MaterialTheme.typography.titleMedium, color = if (totalMinaIng - totalProvIng >= 0) ColorApproved else ColorRejected, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
