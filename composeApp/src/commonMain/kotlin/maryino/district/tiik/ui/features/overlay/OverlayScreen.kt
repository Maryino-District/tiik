package maryino.district.tiik.ui.features.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tiik.composeapp.generated.resources.*
import maryino.district.tiik.ui.components.*
import maryino.district.tiik.ui.theme.*
import org.jetbrains.compose.resources.stringResource

// ─────────────────────────────────────────────────────────────
// Note: This screen is shown as a TYPE_APPLICATION_OVERLAY
// window from BlockerAccessibilityService (Android-only).
// It is a regular @Composable but hosted in a ComposeView
// inside a WindowManager overlay — NOT part of the NavHost.
// ─────────────────────────────────────────────────────────────

@Composable
fun OverlayScreen(
    blockedAppName: String,
    guardianUsername: String,
    isGuardianOnline: Boolean,
    onRequestUnlock: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TiikColors.OverlayBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.screenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            // ── App icon + lock badge ─────────────────────────
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(TiikShapes.xl)
                        .background(TiikColors.OverlayCard)
                        .border(1.dp, TiikColors.OverlayBorder, TiikShapes.xl),
                    contentAlignment = Alignment.Center,
                ) {
                    // In production: AsyncImage with app icon
                    Text("📱", style = MaterialTheme.typography.displayMedium)
                }

                // Lock badge
                Box(
                    modifier = Modifier
                        .offset(x = 6.dp, y = 6.dp)
                        .size(28.dp)
                        .clip(TiikShapes.avatar)
                        .background(TiikColors.BgSurface),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🔒", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(Spacing.xxl))

            // ── Heading ───────────────────────────────────────
            EyebrowText(
                text = stringResource(Res.string.overlay_blocked),
                color = Color.White.copy(alpha = 0.25f),
            )

            Spacer(Modifier.height(Spacing.xs))

            Text(
                text = stringResource(Res.string.overlay_app_locked, blockedAppName),
                style = MaterialTheme.typography.headlineLarge,
                color = TiikColors.InkOnDark,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(Spacing.sm))

            Text(
                text = stringResource(Res.string.overlay_description, guardianUsername),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(Spacing.xxl))

            // ── Guardian pill ─────────────────────────────────
            GuardianRow(
                username = "@$guardianUsername",
                isOnline = isGuardianOnline,
                onDark = true,
            )

            Spacer(Modifier.height(Spacing.xl))

            // ── Actions ───────────────────────────────────────
            // Primary: white button on dark bg
            androidx.compose.material3.Surface(
                onClick = onRequestUnlock,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Spacing.touchTarget),
                shape = TiikShapes.md,
                color = TiikColors.BgSurface,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(Res.string.overlay_request_unlock),
                        style = MaterialTheme.typography.labelLarge,
                        color = TiikColors.Ink,
                    )
                }
            }

            Spacer(Modifier.height(Spacing.sm))

            // Ghost: go back
            androidx.compose.material3.Surface(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Spacing.touchTarget),
                shape = TiikShapes.md,
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    TiikColors.OverlayBorder,
                ),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(Res.string.overlay_go_back),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.3f),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun OverlayScreenPreview() {
    TiikScreenPreview {
        OverlayScreen(
            blockedAppName = "Instagram",
            guardianUsername = "@maksim_k",
            isGuardianOnline = true,
            onRequestUnlock = {},
            onDismiss = {},
        )
    }
}
