package maryino.district.tiik.ui.features.unlock

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import maryino.district.tiik.ui.components.*
import maryino.district.tiik.ui.theme.*

// ─────────────────────────────────────────────────────────────
// Two sub-screens: form + waiting state
// ─────────────────────────────────────────────────────────────

sealed interface UnlockScreenState {
    data object Form : UnlockScreenState
    data object Waiting : UnlockScreenState
    data class Approved(val guardianNote: String?) : UnlockScreenState
    data class Denied(val reason: String?) : UnlockScreenState
}

// ─────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────

@Composable
fun UnlockRequestScreen(
    appName: String,
    guardianUsername: String,
    guardianDisplayName: String,
    screenState: UnlockScreenState,
    onSendRequest: (message: String) -> Unit,
    onBack: () -> Unit,
    onOpenApp: () -> Unit,     // called after Approved
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TiikColors.Bg),
    ) {
        when (screenState) {
            is UnlockScreenState.Form -> RequestForm(
                appName = appName,
                guardianUsername = guardianUsername,
                guardianDisplayName = guardianDisplayName,
                onSend = onSendRequest,
                onBack = onBack,
            )

            is UnlockScreenState.Waiting -> WaitingState(
                guardianUsername = guardianUsername,
            )

            is UnlockScreenState.Approved -> ApprovedState(
                guardianUsername = guardianUsername,
                note = screenState.guardianNote,
                onOpenApp = onOpenApp,
            )

            is UnlockScreenState.Denied -> DeniedState(
                reason = screenState.reason,
                onBack = onBack,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Request form
// ─────────────────────────────────────────────────────────────

@Composable
private fun RequestForm(
    appName: String,
    guardianUsername: String,
    guardianDisplayName: String,
    onSend: (message: String) -> Unit,
    onBack: () -> Unit,
) {
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = Spacing.screenPadding,
                    end = Spacing.screenPadding,
                    top = Spacing.xxl,
                    bottom = Spacing.md,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            TiikSquareIconButton(onClick = onBack) {
                Text("←", style = MaterialTheme.typography.titleMedium, color = TiikColors.Ink)
            }
            Column {
                EyebrowText("Unlock request")
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Ask your guardian",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TiikColors.Ink,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            // Guardian card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(TiikShapes.lg)
                    .background(TiikColors.BgMuted)
                    .padding(Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(TiikShapes.avatar)
                        .background(TiikColors.BgSubtle)
                        .border(1.5.dp, TiikColors.Border, TiikShapes.avatar),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("😎", style = MaterialTheme.typography.bodyLarge)
                }
                Column {
                    Text(
                        text = guardianDisplayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = TiikColors.Ink,
                    )
                    Text(
                        text = "@$guardianUsername · will be notified",
                        style = MaterialTheme.typography.bodySmall,
                        color = TiikColors.Ink3,
                    )
                }
            }

            // App chip
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                EyebrowText("Unlocking")
                Row(
                    modifier = Modifier
                        .clip(TiikShapes.sm)
                        .background(TiikColors.BgMuted)
                        .border(1.dp, TiikColors.Border2, TiikShapes.sm)
                        .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    Text("📱", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = appName,
                        style = MaterialTheme.typography.titleSmall,
                        color = TiikColors.Ink2,
                    )
                }
            }

            // Message field
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                EyebrowText("Why? (optional)")
                TiikTextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = "e.g. Need to check a work DM…",
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                )
            }

            Text(
                text = "@$guardianUsername can approve or deny from their phone.",
                style = MaterialTheme.typography.bodySmall,
                color = TiikColors.Ink3,
            )
        }

        // CTA
        TiikButton(
            text = "Send request →",
            onClick = { onSend(message) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenPadding, vertical = Spacing.lg),
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Waiting state
// ─────────────────────────────────────────────────────────────

@Composable
private fun WaitingState(
    guardianUsername: String,
    modifier: Modifier = Modifier,
) {
    // Pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseOut),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulse1",
    )
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseOut),
            repeatMode = RepeatMode.Restart,
        ),
        label = "alpha1",
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Pulse ring
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale1)
                    .clip(TiikShapes.avatar)
                    .background(TiikColors.BgSubtle.copy(alpha = alpha1))
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(TiikShapes.avatar)
                    .background(TiikColors.BgMuted),
                contentAlignment = Alignment.Center,
            ) {
                Text("📨", style = MaterialTheme.typography.headlineMedium)
            }
        }

        Spacer(Modifier.height(Spacing.xxl))

        Text(
            text = "Request sent",
            style = MaterialTheme.typography.headlineMedium,
            color = TiikColors.Ink,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "Waiting for @$guardianUsername\nto respond.",
            style = MaterialTheme.typography.bodyMedium,
            color = TiikColors.Ink3,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(Spacing.x3l))

        // Status items
        StatusLine(label = "Request delivered", isDone = true)
        Spacer(Modifier.height(Spacing.sm))
        StatusLine(label = "Waiting for approval…", isDone = false)
    }
}

@Composable
private fun StatusLine(label: String, isDone: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(TiikShapes.md)
            .background(TiikColors.BgMuted)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm + 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm + 2.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(TiikShapes.avatar)
                .background(if (isDone) TiikColors.Ink else TiikColors.Border),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isDone) TiikColors.Ink else TiikColors.Ink3,
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Approved state
// ─────────────────────────────────────────────────────────────

@Composable
private fun ApprovedState(
    guardianUsername: String,
    note: String?,
    onOpenApp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Check mark box
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(TiikShapes.xl)
                .background(TiikColors.Ink),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "✓",
                style = MaterialTheme.typography.headlineLarge,
                color = TiikColors.InkOnDark,
            )
        }

        Spacer(Modifier.height(Spacing.xxl))

        Text(
            text = "Unlocked!",
            style = MaterialTheme.typography.headlineLarge,
            color = TiikColors.Ink,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "@$guardianUsername approved your request.",
            style = MaterialTheme.typography.bodyMedium,
            color = TiikColors.Ink3,
            textAlign = TextAlign.Center,
        )

        if (!note.isNullOrBlank()) {
            Spacer(Modifier.height(Spacing.xxl))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(TiikShapes.lg)
                    .background(TiikColors.BgMuted)
                    .border(1.dp, TiikColors.Border, TiikShapes.lg)
                    .padding(Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(TiikShapes.avatar)
                        .background(TiikColors.BgSubtle),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("😎", style = MaterialTheme.typography.bodyLarge)
                }
                Column {
                    Text(
                        text = "@$guardianUsername",
                        style = MaterialTheme.typography.titleSmall,
                        color = TiikColors.Ink,
                    )
                    Text(
                        text = "\"$note\"",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        color = TiikColors.Ink3,
                    )
                }
            }
        }

        Spacer(Modifier.height(Spacing.x3l))

        TiikButton(
            text = "Open app",
            onClick = onOpenApp,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Denied state
// ─────────────────────────────────────────────────────────────

@Composable
private fun DeniedState(
    reason: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(TiikShapes.xl)
                .background(TiikColors.BgMuted)
                .border(1.dp, TiikColors.Border, TiikShapes.xl),
            contentAlignment = Alignment.Center,
        ) {
            Text("✕", style = MaterialTheme.typography.headlineLarge, color = TiikColors.Ink3)
        }

        Spacer(Modifier.height(Spacing.xxl))

        Text(
            text = "Request denied",
            style = MaterialTheme.typography.headlineMedium,
            color = TiikColors.Ink,
        )
        Spacer(Modifier.height(Spacing.sm))

        if (!reason.isNullOrBlank()) {
            Text(
                text = "\"$reason\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                color = TiikColors.Ink3,
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                text = "Your guardian said no.\nStay focused — you've got this.",
                style = MaterialTheme.typography.bodyMedium,
                color = TiikColors.Ink3,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(Spacing.x3l))

        TiikButton(
            text = "← Back",
            onClick = onBack,
            style = TiikButtonStyle.Light,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
private fun UnlockRequestFormPreview() {
    TiikScreenPreview {
        UnlockRequestScreen(
            appName = "Instagram",
            guardianUsername = "maksim_k",
            guardianDisplayName = "Maksim K.",
            screenState = UnlockScreenState.Form,
            onSendRequest = {},
            onBack = {},
            onOpenApp = {},
        )
    }
}

@Preview
@Composable
private fun UnlockRequestApprovedPreview() {
    TiikScreenPreview {
        UnlockRequestScreen(
            appName = "Instagram",
            guardianUsername = "maksim_k",
            guardianDisplayName = "Maksim K.",
            screenState = UnlockScreenState.Approved("Take 10 minutes."),
            onSendRequest = {},
            onBack = {},
            onOpenApp = {},
        )
    }
}

@Preview
@Composable
private fun UnlockRequestDeniedPreview() {
    TiikScreenPreview {
        UnlockRequestScreen(
            appName = "Instagram",
            guardianUsername = "maksim_k",
            guardianDisplayName = "Maksim K.",
            screenState = UnlockScreenState.Denied("Not worth breaking the streak."),
            onSendRequest = {},
            onBack = {},
            onOpenApp = {},
        )
    }
}
