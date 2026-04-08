package maryino.district.tiik.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tiik.composeapp.generated.resources.*
import maryino.district.tiik.ui.theme.*
import org.jetbrains.compose.resources.stringResource

// ─────────────────────────────────────────────────────────────
// TiikButton
// ─────────────────────────────────────────────────────────────

enum class TiikButtonStyle { Fill, Light, Ghost }

@Composable
fun TiikButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: TiikButtonStyle = TiikButtonStyle.Fill,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(100),
        label = "button_scale"
    )

    val bgColor = when (style) {
        TiikButtonStyle.Fill  -> if (enabled) TiikColors.Ink else TiikColors.BgSubtle
        TiikButtonStyle.Light -> TiikColors.BgMuted
        TiikButtonStyle.Ghost -> Color.Transparent
    }
    val textColor = when (style) {
        TiikButtonStyle.Fill  -> if (enabled) TiikColors.InkOnDark else TiikColors.Ink3
        TiikButtonStyle.Light -> TiikColors.Ink2
        TiikButtonStyle.Ghost -> TiikColors.Ink3
    }
    val border = when (style) {
        TiikButtonStyle.Ghost -> BorderStroke(1.dp, TiikColors.Border)
        else -> null
    }

    Surface(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .height(Spacing.touchTarget),
        enabled = enabled,
        shape = TiikShapes.md,
        color = bgColor,
        border = border,
        interactionSource = interactionSource,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// TiikIconButton  (back arrow, FAB-like square buttons)
// ─────────────────────────────────────────────────────────────

@Composable
fun TiikSquareIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    shape: Shape = TiikShapes.sm,
    icon: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(size),
        shape = shape,
        color = TiikColors.BgMuted,
        border = BorderStroke(1.dp, TiikColors.Border),
    ) {
        Box(contentAlignment = Alignment.Center) {
            icon()
        }
    }
}

// ─────────────────────────────────────────────────────────────
// TiikTextField
// ─────────────────────────────────────────────────────────────

@Composable
fun TiikTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    singleLine: Boolean = true,
) {
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TiikColors.Ink3,
                modifier = Modifier.padding(bottom = Spacing.xs)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TiikColors.Ink3,
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TiikColors.Ink),
            singleLine = singleLine,
            shape = TiikShapes.md,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = TiikColors.Ink,
                unfocusedBorderColor = TiikColors.Border,
                focusedContainerColor   = TiikColors.BgSurface,
                unfocusedContainerColor = TiikColors.BgMuted,
                cursorColor = TiikColors.Ink,
            ),
            modifier = modifier.fillMaxWidth(),
        )
    }
}

// ─────────────────────────────────────────────────────────────
// TiikCard
// ─────────────────────────────────────────────────────────────

@Composable
fun TiikCard(
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,  // border = ink when active block
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val borderColor = if (highlighted) TiikColors.BorderStrong else TiikColors.Border

    val surfaceModifier = if (onClick != null) modifier else modifier
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = surfaceModifier,
        shape = TiikShapes.lg,
        color = TiikColors.BgSurface,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            content = content,
        )
    }
}

// ─────────────────────────────────────────────────────────────
// TiikBadge
// ─────────────────────────────────────────────────────────────

enum class TiikBadgeStyle { Dark, Light, Success, Danger }

@Composable
fun TiikBadge(
    text: String,
    style: TiikBadgeStyle = TiikBadgeStyle.Light,
) {
    val bg = when (style) {
        TiikBadgeStyle.Dark    -> TiikColors.Ink
        TiikBadgeStyle.Light   -> TiikColors.BgMuted
        TiikBadgeStyle.Success -> TiikColors.Success.copy(alpha = 0.12f)
        TiikBadgeStyle.Danger  -> TiikColors.Danger.copy(alpha = 0.10f)
    }
    val textColor = when (style) {
        TiikBadgeStyle.Dark    -> TiikColors.InkOnDark
        TiikBadgeStyle.Light   -> TiikColors.Ink3
        TiikBadgeStyle.Success -> TiikColors.Success
        TiikBadgeStyle.Danger  -> TiikColors.Danger
    }

    Box(
        modifier = Modifier
            .clip(TiikShapes.pill)
            .background(bg)
            .padding(horizontal = Spacing.sm, vertical = 3.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
        )
    }
}

// ─────────────────────────────────────────────────────────────
// TiikChip  (tab-like selector)
// ─────────────────────────────────────────────────────────────

@Composable
fun TiikChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg        = if (selected) TiikColors.Ink        else Color.Transparent
    val textColor = if (selected) TiikColors.InkOnDark  else TiikColors.Ink3
    val border    = if (selected) TiikColors.Ink         else TiikColors.Border

    Box(
        modifier = modifier
            .clip(TiikShapes.pill)
            .background(bg)
            .border(1.dp, border, TiikShapes.pill)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.xs + 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
        )
    }
}

// ─────────────────────────────────────────────────────────────
// TiikDivider
// ─────────────────────────────────────────────────────────────

@Composable
fun TiikDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = TiikColors.Border2,
    )
}

// ─────────────────────────────────────────────────────────────
// EyebrowText  (small uppercase label above headings)
// ─────────────────────────────────────────────────────────────

@Composable
fun EyebrowText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TiikColors.Ink3,
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier,
    )
}

// ─────────────────────────────────────────────────────────────
// StepDots  (onboarding / add-block stepper)
// ─────────────────────────────────────────────────────────────

@Composable
fun StepDots(
    totalSteps: Int,
    currentStep: Int,   // 0-indexed
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalSteps) { index ->
            val isActive = index == currentStep
            val width by animateFloatAsState(
                targetValue = if (isActive) 18f else 6f,
                animationSpec = tween(250),
                label = "dot_width_$index"
            )
            Box(
                modifier = Modifier
                    .width(width.dp)
                    .height(6.dp)
                    .clip(TiikShapes.pill)
                    .background(if (isActive) TiikColors.Ink else TiikColors.Border)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// AppRow  (app icon + name + meta — used in Blocks and AddBlock)
// ─────────────────────────────────────────────────────────────

@Composable
fun AppRow(
    appName: String,
    appIcon: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // App icon container
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(TiikShapes.appIcon)
                .background(TiikColors.BgMuted)
                .border(1.dp, TiikColors.Border2, TiikShapes.appIcon),
            contentAlignment = Alignment.Center,
            content = appIcon,
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = appName,
                style = MaterialTheme.typography.titleSmall,
                color = TiikColors.Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TiikColors.Ink3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        trailing?.invoke()
    }
}

// ─────────────────────────────────────────────────────────────
// GuardianRow  (avatar + name + role — overlay + request screens)
// ─────────────────────────────────────────────────────────────

@Composable
fun GuardianRow(
    username: String,
    modifier: Modifier = Modifier,
    isOnline: Boolean = false,
    onDark: Boolean = false,
    role: String? = null,
) {
    val bg          = if (onDark) Color(0x0DFFFFFF) else TiikColors.BgMuted
    val borderColor = if (onDark) Color(0x1AFFFFFF) else TiikColors.Border
    val nameColor   = if (onDark) TiikColors.InkOnDark else TiikColors.Ink
    val roleColor   = if (onDark) Color(0x47FFFFFF) else TiikColors.Ink3
    val resolvedRole = role ?: stringResource(Res.string.components_guardian_role)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = TiikShapes.md,
        color = bg,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm + 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm + 2.dp),
        ) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(TiikShapes.avatar)
                    .background(if (onDark) Color(0xFF2A2A2A) else TiikColors.BgSubtle),
                contentAlignment = Alignment.Center,
            ) {
                Text("😎", style = MaterialTheme.typography.bodyMedium)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(username, style = MaterialTheme.typography.titleSmall, color = nameColor)
                Text(resolvedRole, style = MaterialTheme.typography.bodySmall, color = roleColor)
            }

            if (isOnline) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(TiikShapes.avatar)
                            .background(TiikColors.Success)
                    )
                    Text(
                        text = stringResource(Res.string.common_online),
                        style = MaterialTheme.typography.labelSmall,
                        color = TiikColors.Success,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// SettingRow  (profile screen settings list item)
// ─────────────────────────────────────────────────────────────

@Composable
fun SettingRow(
    title: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingValue: String? = null,
    showArrow: Boolean = true,
    titleColor: Color = TiikColors.Ink,
    onClick: (() -> Unit)? = null,
) {
    val rowModifier = if (onClick != null)
        modifier.clickable(onClick = onClick)
    else modifier

    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .height(Spacing.touchTarget + 4.dp)
            .padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm + 2.dp),
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(TiikShapes.xs)
                .background(TiikColors.BgMuted),
            contentAlignment = Alignment.Center,
        ) {
            icon()
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = titleColor)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TiikColors.Ink3)
            }
        }

        if (trailingValue != null) {
            Text(trailingValue, style = MaterialTheme.typography.labelMedium, color = TiikColors.Ink3)
        } else if (showArrow) {
            Text("›", style = MaterialTheme.typography.titleLarge, color = TiikColors.Ink3)
        }
    }
}

@Preview
@Composable
private fun TiikButtonPreview() {
    TiikComponentPreview {
        TiikButton(
            text = stringResource(Res.string.common_continue),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun TiikCardPreview() {
    TiikComponentPreview {
        TiikCard {
            Text(
                text = stringResource(Res.string.components_preview_card),
                style = MaterialTheme.typography.titleLarge,
                color = TiikColors.Ink,
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(Res.string.components_preview_card_description),
                style = MaterialTheme.typography.bodyMedium,
                color = TiikColors.Ink3,
            )
        }
    }
}

@Preview
@Composable
private fun TiikTextFieldPreview() {
    TiikComponentPreview {
        TiikTextField(
            value = "alex@example.com",
            onValueChange = {},
            placeholder = stringResource(Res.string.common_email_label),
            label = stringResource(Res.string.common_email_label),
        )
    }
}

@Preview
@Composable
private fun ComponentRowsPreview() {
    TiikComponentPreview {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            AppRow(
                appName = "Instagram",
                subtitle = "@maksim_k",
                appIcon = { Text("\uD83D\uDCF1") },
                trailing = {
                    TiikBadge(
                        text = stringResource(Res.string.blocks_status_locked),
                        style = TiikBadgeStyle.Dark,
                    )
                },
            )
            GuardianRow(
                username = "@maksim_k",
                isOnline = true,
            )
            SettingRow(
                title = stringResource(Res.string.profile_notifications),
                subtitle = stringResource(Res.string.components_notifications_critical),
                icon = { Text("\uD83D\uDD14") },
                trailingValue = stringResource(Res.string.profile_on),
            )
        }
    }
}
