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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mars.madereraapp.data.local.entities.RequerimientoEntity
import com.mars.madereraapp.ui.components.*
import com.mars.madereraapp.ui.theme.*
import kotlinx.coroutines.delay
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
    val supervisores by viewModel.supervisores.collectAsState()
    val hiddenCount by viewModel.hiddenCount.collectAsState()

    val filtroEstado by viewModel.filtroEstado.collectAsState()
    val filtroMina by viewModel.filtroMina.collectAsState()
    val filtroSupervisor by viewModel.filtroSupervisor.collectAsState()
    val filtroMes by viewModel.filtroMes.collectAsState()
    val filtroAnio by viewModel.filtroAnio.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val aniosDisponibles by viewModel.aniosDisponibles.collectAsState()

    var expandedEstado by remember { mutableStateOf(false) }
    var expandedMina by remember { mutableStateOf(false) }
    var expandedSupervisor by remember { mutableStateOf(false) }
    var expandedMes by remember { mutableStateOf(false) }
    var expandedAnio by remember { mutableStateOf(false) }

    val mesesOpciones = listOf(
        "" to "Todos", "01" to "Enero", "02" to "Febrero", "03" to "Marzo",
        "04" to "Abril", "05" to "Mayo", "06" to "Junio", "07" to "Julio",
        "08" to "Agosto", "09" to "Septiembre", "10" to "Octubre",
        "11" to "Noviembre", "12" to "Diciembre"
    )

    val estadoOpciones = listOf("TODOS", "PENDIENTE", "PARCIAL", "COMPLETADO")
    val estadoLabels = mapOf("TODOS" to "Todos", "PENDIENTE" to "Pendientes", "PARCIAL" to "Parciales", "COMPLETADO" to "Completados")

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Requerimientos",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        val subtitle = buildString {
                            append("${requerimientos.size} registros")
                            if (hiddenCount > 0) {
                                append(" · $hiddenCount ocultos")
                            }
                        }
                        Text(
                            subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (hiddenCount > 0) PrimaryWood else TextTertiary
                        )
                    }
                },
                actions = {
                    if (hiddenCount > 0) {
                        TextButton(
                            onClick = { viewModel.unhideAll() }
                        ) {
                            Icon(
                                Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = PrimaryWood,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Mostrar $hiddenCount",
                                color = PrimaryWood,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        }
    ) { padding ->
        val snackbarHostState = remember { SnackbarHostState() }
        var isRefreshing by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        Scaffold(
            containerColor = BackgroundLight,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->

            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Row 1: Estado + Mina
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = expandedEstado,
                            onExpandedChange = { expandedEstado = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = estadoLabels[filtroEstado] ?: "Todos",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Estado", style = MaterialTheme.typography.labelSmall) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstado) },
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
                                expanded = expandedEstado,
                                onDismissRequest = { expandedEstado = false }
                            ) {
                                estadoOpciones.forEach { opcion ->
                                    DropdownMenuItem(
                                        text = { Text(estadoLabels[opcion] ?: opcion) },
                                        onClick = { viewModel.filtroEstado.value = opcion; expandedEstado = false }
                                    )
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = expandedMina,
                            onExpandedChange = { expandedMina = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = if (filtroMina == "TODAS") "Todas" else filtroMina,
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
                                    onClick = { viewModel.filtroMina.value = "TODAS"; expandedMina = false }
                                )
                                minas.forEach { mina ->
                                    DropdownMenuItem(
                                        text = { Text(mina.nombre) },
                                        onClick = { viewModel.filtroMina.value = mina.nombre; expandedMina = false }
                                    )
                                }
                            }
                        }
                    }

                    // Row 2: Supervisor + Mes + Año
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = expandedSupervisor,
                            onExpandedChange = { expandedSupervisor = it },
                            modifier = Modifier.weight(1.2f)
                        ) {
                            OutlinedTextField(
                                value = if (filtroSupervisor == "TODOS") "Todos" else filtroSupervisor,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Supervisor", style = MaterialTheme.typography.labelSmall) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSupervisor) },
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
                                expanded = expandedSupervisor,
                                onDismissRequest = { expandedSupervisor = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Todos") },
                                    onClick = { viewModel.filtroSupervisor.value = "TODOS"; expandedSupervisor = false }
                                )
                                supervisores.forEach { sup ->
                                    DropdownMenuItem(
                                        text = { Text(sup.nombre) },
                                        onClick = { viewModel.filtroSupervisor.value = sup.nombre; expandedSupervisor = false }
                                    )
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = expandedMes,
                            onExpandedChange = { expandedMes = it },
                            modifier = Modifier.weight(0.9f)
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
                                        onClick = { viewModel.filtroMes.value = valor; expandedMes = false }
                                    )
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = expandedAnio,
                            onExpandedChange = { expandedAnio = it },
                            modifier = Modifier.weight(0.8f)
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
                                    onClick = { viewModel.filtroAnio.value = ""; expandedAnio = false }
                                )
                                aniosDisponibles.forEach { anio ->
                                    DropdownMenuItem(
                                        text = { Text(anio) },
                                        onClick = { viewModel.filtroAnio.value = anio; expandedAnio = false }
                                    )
                                }
                            }
                        }
                    }

                    // Row 3: Buscar por código de requerimiento
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar por código...", color = TextTertiary) },
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
                }

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
                        subtitle = if (hiddenCount > 0) "$hiddenCount ocultos — toca \"Mostrar\" arriba" else "Desliza hacia abajo para actualizar"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(
                            items = requerimientos,
                            key = { _, req -> req.localId }
                        ) { _, req ->
                            SwipeableRequerimientoItem(
                                req = req,
                                onClick = {
                                    val sid = req.serverId
                                    if (sid != null) onNavigateToDetail(sid)
                                },
                                onHide = { viewModel.hideRequerimiento(req.localId) }
                            )
                        }
                    }
                }
            }
            }
        }
    }
}

/**
 * Wraps a RequerimientoCard with SwipeToDismissBox.
 * Only COMPLETADO items can be swiped to hide.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableRequerimientoItem(
    req: RequerimientoEntity,
    onClick: () -> Unit,
    onHide: () -> Unit
) {
    val isCompletado = req.estado == "COMPLETADO"

    if (!isCompletado) {
        // Non-completado items are not swipeable
        RequerimientoCard(req, onClick)
        return
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd) {
                onHide()
                true
            } else {
                false
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.4f }
    )

    // Animate exit when dismissed
    AnimatedVisibility(
        visible = dismissState.currentValue == SwipeToDismissBoxValue.Settled,
        exit = shrinkVertically(
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(200))
    ) {
        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = false,
            backgroundContent = {
                SwipeHideBackground(dismissState)
            }
        ) {
            RequerimientoCard(req, onClick)
        }
    }

    // If already dismissed (not Settled), just call the card invisible
    if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
        // Item is being removed, the AnimatedVisibility handles the exit
    }
}

/**
 * Background shown while swiping — green with check icon and "Ocultar" text.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeHideBackground(dismissState: SwipeToDismissBoxState) {
    val progress = dismissState.progress

    val backgroundColor = ColorApproved.copy(alpha = (progress * 0.25f).coerceIn(0f, 0.2f))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.alpha(progress.coerceIn(0f, 1f))
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Ocultar",
                tint = ColorApproved,
                modifier = Modifier.size(24.dp)
            )
            Text(
                "Ocultar",
                color = ColorApproved,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
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
                    style = MaterialTheme.typography.bodyMedium, // Más pequeño
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
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

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = DividerColor.copy(alpha = 0.5f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = req.fecha, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Terrain, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = req.minaNombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                req.supervisorNombre?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = it, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }

                if (req.total_proveedor > 0 || req.total_mina > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "T. Prov: S/ ${"%.2f".format(req.total_proveedor)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryWood, // Highlight
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "T. Mina: S/ ${"%.2f".format(req.total_mina)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = ColorApproved, // Highlight
                            fontWeight = FontWeight.ExtraBold
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
