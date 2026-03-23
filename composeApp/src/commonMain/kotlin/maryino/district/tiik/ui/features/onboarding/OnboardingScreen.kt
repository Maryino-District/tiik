package maryino.district.tiik.ui.features.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import maryino.district.tiik.ui.components.*
import maryino.district.tiik.ui.theme.*

// ─────────────────────────────────────────────────────────────
// Data model
// ─────────────────────────────────────────────────────────────

data class PermissionItem(
    val emoji: String,
    val title: String,
    val state: PermissionState,
)

enum class PermissionState { Done, Active, Idle }

// ─────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────

@Composable
fun OnboardingScreen(
    permissions: List<PermissionItem>,
    currentStep: Int,          // 0-indexed, 0..2
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TiikColors.Bg)
            .padding(horizontal = Spacing.screenPadding),
    ) {
        Spacer(Modifier.height(Spacing.x3l))

        // ── Hero visual ──────────────────────────────────────
        OnboardingHero(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        )

        Spacer(Modifier.height(Spacing.xxl))

        // ── Heading ──────────────────────────────────────────
        EyebrowText(
            text = "Setup · step ${currentStep + 1} of ${permissions.size}",
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = "A few permissions\nto get started",
            style = MaterialTheme.typography.headlineMedium,
            color = TiikColors.Ink,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "tiik needs these to keep your blocks running when you switch apps.",
            style = MaterialTheme.typography.bodyMedium,
            color = TiikColors.Ink3,
        )

        Spacer(Modifier.height(Spacing.xl))

        // ── Permission list ───────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            permissions.forEach { item ->
                PermissionRow(item = item)
            }
        }

        Spacer(Modifier.weight(1f))

        // ── CTA ───────────────────────────────────────────────
        TiikButton(
            text = permissions.getOrNull(currentStep)
                ?.let { "Allow ${it.title}" }
                ?: "Continue",
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.x3l))
    }
}

// ─────────────────────────────────────────────────────────────
// Subcomponents
// ─────────────────────────────────────────────────────────────

@Composable
private fun OnboardingHero(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(TiikShapes.xl)
            .background(TiikColors.BgMuted)
            .border(1.dp, TiikColors.Border, TiikShapes.xl),
        contentAlignment = Alignment.Center,
    ) {
        // Concentric rings
        repeat(3) { index ->
            val size = 64.dp + (index * 38).dp
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(TiikShapes.avatar)
                    .border(
                        width = 1.dp,
                        color = TiikColors.Border.copy(alpha = 0.4f - index * 0.1f),
                        shape = TiikShapes.avatar,
                    )
            )
        }

        // Lock icon box
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(TiikShapes.lg)
                .background(TiikColors.Ink),
            contentAlignment = Alignment.Center,
        ) {
            Text("🔒", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun PermissionRow(item: PermissionItem) {
    val bg = when (item.state) {
        PermissionState.Done   -> TiikColors.BgMuted
        PermissionState.Active -> TiikColors.Ink
        PermissionState.Idle   -> TiikColors.BgMuted
    }
    val alpha = if (item.state == PermissionState.Idle) 0.45f else 1f
    val titleColor = when (item.state) {
        PermissionState.Active -> TiikColors.InkOnDark
        else -> TiikColors.Ink
    }
    val trailingText = when (item.state) {
        PermissionState.Done   -> "✓"
        PermissionState.Active -> "→"
        PermissionState.Idle   -> ""
    }
    val trailingColor = when (item.state) {
        PermissionState.Done   -> TiikColors.Success
        PermissionState.Active -> Color.White.copy(alpha = 0.4f)
        PermissionState.Idle   -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(TiikShapes.md)
            .background(bg)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm + 2.dp)
            .then(
                if (item.state == PermissionState.Idle)
                    Modifier // alpha handled by bg
                else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(TiikShapes.xs)
                .background(
                    when (item.state) {
                        PermissionState.Active -> Color.White.copy(alpha = 0.12f)
                        else -> TiikColors.Border.copy(alpha = 0.5f)
                    }
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(item.emoji, style = MaterialTheme.typography.bodyMedium)
        }

        Text(
            text = item.title,
            style = MaterialTheme.typography.titleSmall,
            color = titleColor,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = trailingText,
            style = MaterialTheme.typography.titleSmall,
            color = trailingColor,
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Preview data helper
// ─────────────────────────────────────────────────────────────

fun samplePermissions() = listOf(
    PermissionItem("📊", "Usage stats",    PermissionState.Done),
    PermissionItem("♿", "Accessibility",  PermissionState.Done),
    PermissionItem("🔔", "Notifications",  PermissionState.Active),
)

@Preview
@Composable
private fun OnboardingScreenPreview() {
    TiikScreenPreview {
        OnboardingScreen(
            permissions = samplePermissions(),
            currentStep = 2,
            onRequestPermission = {},
        )
    }
}
