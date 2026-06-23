package com.mars.madereraapp.ui.ingresos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.data.local.entities.IngresoEntity
import com.mars.madereraapp.ui.components.*
import com.mars.madereraapp.ui.requerimientos.DetailRow
import com.mars.madereraapp.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresoListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Int) -> Unit = {},
    viewModel: IngresoViewModel = hiltViewModel()
) {
    val ingresos by viewModel.ingresos.collectAsState()
    val ordenActual by viewModel.ordenActual.collectAsState()

    // FAB animation
    var fabVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(400)
        fabVisible = true
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Ingresos de Stock",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (ordenActual == OrdenIngreso.POR_VALE)
                                "Ordenado por N° vale"
                            else
                                "${ingresos.size} registros · por fecha",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleOrden() }) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Cambiar orden",
                            tint = if (ordenActual == OrdenIngreso.POR_VALE) PrimaryWood else TextTertiary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible,
                enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn()
            ) {
                FloatingActionButton(
                    onClick = onNavigateToCreate,
                    containerColor = PrimaryWood,
                    contentColor = TextOnPrimary,
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Registrar Ingreso")
                }
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
            val filtroMina by viewModel.filtroMina.collectAsState()
            val filtroViaje by viewModel.filtroViaje.collectAsState()
            val filtroVale by viewModel.filtroVale.collectAsState()
            val filtroMes by viewModel.filtroMes.collectAsState()
            val filtroAnio by viewModel.filtroAnio.collectAsState()
            val minasDisponibles by viewModel.minasDisponibles.collectAsState()
            val viajesDisponibles by viewModel.viajesDisponibles.collectAsState()
            val aniosDisponibles by viewModel.aniosDisponibles.collectAsState()

            var expandedMina by remember { mutableStateOf(false) }
            var expandedViaje by remember { mutableStateOf(false) }
            var expandedMes by remember { mutableStateOf(false) }
            var expandedAnio by remember { mutableStateOf(false) }

            val mesesOpciones = listOf(
                "" to "Todos", "01" to "Enero", "02" to "Febrero", "03" to "Marzo",
                "04" to "Abril", "05" to "Mayo", "06" to "Junio", "07" to "Julio",
                "08" to "Agosto", "09" to "Septiembre", "10" to "Octubre",
                "11" to "Noviembre", "12" to "Diciembre"
            )


            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = expandedMina,
                            onExpandedChange = { expandedMina = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = filtroMina.ifBlank { "Todas" },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Mina", style = MaterialTheme.typography.labelSmall) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMina) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = PrimaryWood,
                                    unfocusedBorderColor = DividerColor,
                                    focusedContainerColor = SurfaceContainer,
                                    unfocusedContainerColor = SurfaceContainer
                                ),
                                modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedMina,
                                onDismissRequest = { expandedMina = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Todas") },
                                    onClick = { viewModel.updateFiltroMina(""); expandedMina = false }
                                )
                                minasDisponibles.forEach { mina ->
                                    DropdownMenuItem(
                                        text = { Text(mina) },
                                        onClick = { viewModel.updateFiltroMina(mina); expandedMina = false }
                                    )
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = expandedViaje,
                            onExpandedChange = { expandedViaje = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = if (filtroViaje.isBlank()) "Todos" else "Viaje-$filtroViaje",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Viaje", style = MaterialTheme.typography.labelSmall) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedViaje) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = PrimaryWood,
                                    unfocusedBorderColor = DividerColor,
                                    focusedContainerColor = SurfaceContainer,
                                    unfocusedContainerColor = SurfaceContainer
                                ),
                                modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedViaje,
                                onDismissRequest = { expandedViaje = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Todos") },
                                    onClick = { viewModel.updateFiltroViaje(""); expandedViaje = false }
                                )
                                viajesDisponibles.forEach { v ->
                                    DropdownMenuItem(
                                        text = { Text("Viaje-$v") },
                                        onClick = { viewModel.updateFiltroViaje(v); expandedViaje = false }
                                    )
                                }
                            }
                        }
                    }

                    // Row 2: Mes + Año
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = expandedMes,
                            onExpandedChange = { expandedMes = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = mesesOpciones.firstOrNull { it.first == filtroMes }?.second ?: "Todos",
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
                                modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedMes,
                                onDismissRequest = { expandedMes = false }
                            ) {
                                mesesOpciones.forEach { (valor, nombre) ->
                                    DropdownMenuItem(
                                        text = { Text(nombre) },
                                        onClick = { viewModel.updateFiltroMes(valor); expandedMes = false }
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
                                modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedAnio,
                                onDismissRequest = { expandedAnio = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Todos") },
                                    onClick = { viewModel.updateFiltroAnio(""); expandedAnio = false }
                                )
                                aniosDisponibles.forEach { anio ->
                                    DropdownMenuItem(
                                        text = { Text(anio) },
                                        onClick = { viewModel.updateFiltroAnio(anio); expandedAnio = false }
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = filtroVale,
                        onValueChange = { viewModel.updateFiltroVale(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar por vale...", color = TextTertiary) },
                        leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null, tint = PrimaryWood) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryWood,
                            unfocusedBorderColor = DividerColor,
                            focusedContainerColor = SurfaceContainer,
                            unfocusedContainerColor = SurfaceContainer
                        )
                    )
                }

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        scope.launch {
                            isRefreshing = true
                            viewModel.refresh()
                            isRefreshing = false
                            snackbarHostState.showSnackbar(
                                message = "✓ Datos de ingresos actualizados",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                if (ingresos.isEmpty()) {
                    EmptyStateBox(
                        icon = Icons.Default.Inventory,
                        title = "Sin ingresos registrados",
                        subtitle = "Desliza hacia abajo para actualizar"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(ingresos) { _, ing ->
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
    }
}

@Composable
fun IngresoCard(ing: IngresoEntity, onClick: () -> Unit) {
    val isClickable = ing.serverId != null

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
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ing.codigo_ingreso ?: "Pendiente sync",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary
                )
                if (ing.isPendingSync) {
                    StatusBadge("Pendiente", ColorPending)
                } else {
                    StatusBadge("Sincronizado", ColorApproved)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryWood, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = ing.fecha, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                
                ing.minas?.let {
                    if (it.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Landscape, contentDescription = null, tint = PrimaryWood, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Mina: $it", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                    }
                }

                ing.viaje?.let { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = PrimaryWood, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Viaje: $it", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                }

                ing.vale?.let { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = PrimaryWood, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Vale: $it", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                }

                if (ing.total_proveedor > 0 || ing.total_mina > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "T. Prov: S/ ${"%.2f".format(ing.total_proveedor)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryWood,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "T. Mina: S/ ${"%.2f".format(ing.total_mina)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = ColorApproved,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
