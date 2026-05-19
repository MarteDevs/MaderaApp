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
 * Tarjeta minimalista con sombra suave.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = SurfaceLight,
        shadowElevation = 2.dp,
        tonalElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

/**
 * Botón primario con degradado cálido.
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
            .height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryWood,
            contentColor = TextOnPrimary,
            disabledContainerColor = PrimaryWood.copy(alpha = 0.4f),
            disabledContentColor = TextOnPrimary.copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp
        ),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        content()
    }
}

/**
 * Badge de estado con estilo pill minimalista.
 */
@Composable
fun StatusBadge(
    text: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = statusColor.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = statusColor,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * TextField con estilo limpio para formularios.
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
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        placeholder = { Text(placeholder, color = TextTertiary) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(12.dp),
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryWood,
            unfocusedBorderColor = BorderLight,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = PrimaryWood,
            focusedContainerColor = SurfaceLight,
            unfocusedContainerColor = SurfaceLight,
            focusedLabelColor = PrimaryWood,
            unfocusedLabelColor = TextSecondary,
            errorBorderColor = ColorRejected,
            disabledBorderColor = DividerColor,
            disabledTextColor = TextTertiary
        )
    )
}

/**
 * Dropdown selector con estilo limpio.
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
            label = { Text(label, style = MaterialTheme.typography.labelMedium) },
            trailingIcon = {
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (expanded) PrimaryWood else TextSecondary
                )
            },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryWood,
                unfocusedBorderColor = BorderLight,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = PrimaryWood,
                focusedContainerColor = SurfaceLight,
                unfocusedContainerColor = SurfaceLight,
                focusedLabelColor = PrimaryWood,
                unfocusedLabelColor = TextSecondary
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            containerColor = SurfaceLight,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            optionLabel(option),
                            color = if (option == selectedOption) PrimaryWood else TextPrimary,
                            fontWeight = if (option == selectedOption) FontWeight.SemiBold else FontWeight.Normal
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
        initialValue = 0.4f,
        targetValue = 0.7f,
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
                modifier = Modifier.size(56.dp),
                tint = TextTertiary.copy(alpha = alpha)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
        }
    }
}

/**
 * Encabezado de sección minimalista.
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
                .height(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(PrimaryWood)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
        )
    }
}
