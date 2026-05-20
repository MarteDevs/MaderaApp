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
                            "${ingresos.size} registros",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
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
            val searchQuery by viewModel.searchQuery.collectAsState()

            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                    placeholder = { Text("Buscar por viaje, mina o vale...", color = TextTertiary) },
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
