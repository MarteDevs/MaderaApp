package com.mars.madereraapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mars.madereraapp.ui.theme.*

/**
 * Tarjeta con estilo Glassmorphism para el diseño Industrial Excellence.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(GlassSurface)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(GlassWhite, Color.Transparent, Color.Transparent)
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            content()
        }
    }
}

/**
 * Botón con estilo industrial y degradado ámbar/oro.
 */
@Composable
fun IndustrialButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp)),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextOnPrimary
        ),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(PrimaryAmber, PrimaryGold)
                    )
                )
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                content()
            }
        }
    }
}

/**
 * Badge de estado con estilo premium.
 */
@Composable
fun StatusBadge(
    text: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(50)),
        color = statusColor.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
    ) {
        Text(
            text = text.uppercase(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = statusColor
        )
    }
}

/**
 * TextField con estilo Glass para formularios industriales.
 */
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions = androidx.compose.foundation.text.KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label.uppercase(), style = MaterialTheme.typography.labelSmall) },
        placeholder = { Text(placeholder, color = TextSecondary.copy(alpha = 0.5f)) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(14.dp),
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryAmber,
            unfocusedBorderColor = GlassWhite,
            focusedTextColor = Color.White,
            unfocusedTextColor = TextPrimary,
            cursorColor = PrimaryAmber,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedLabelColor = PrimaryAmber,
            unfocusedLabelColor = TextSecondary,
            errorBorderColor = ColorRejected,
            disabledBorderColor = GlassWhite.copy(alpha = 0.5f),
            disabledTextColor = TextSecondary
        )
    )
}

/**
 * Dropdown selector con estilo Glass para formularios industriales.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GlassDropdown(
    label: String,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {}
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = if (selectedOption != null) optionLabel(selectedOption) else "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label.uppercase(), style = MaterialTheme.typography.labelSmall) },
            trailingIcon = {
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (expanded) PrimaryAmber else TextSecondary
                )
            },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryAmber,
                unfocusedBorderColor = GlassWhite,
                focusedTextColor = Color.White,
                unfocusedTextColor = TextPrimary,
                cursorColor = PrimaryAmber,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedLabelColor = PrimaryAmber,
                unfocusedLabelColor = TextSecondary
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            containerColor = SurfaceContainer,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassWhite)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            optionLabel(option),
                            color = if (option == selectedOption) PrimaryAmber else TextPrimary,
                            fontWeight = if (option == selectedOption) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        onExpandedChange(false)
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = TextPrimary
                    )
                )
            }
        }
    }
}

/**
 * Estado vacío con icono y mensaje.
 */
@Composable
fun EmptyStateBox(
    icon: ImageVector,
    title: String,
    subtitle: String = "",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "emptyState")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emptyAlpha"
    )

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = TextSecondary.copy(alpha = alpha)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                title.uppercase(),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.1.sp
            )
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    subtitle.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary.copy(0.4f)
                )
            }
        }
    }
}

/**
 * Encabezado de sección con línea decorativa.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(PrimaryAmber)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            letterSpacing = 0.08.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
