package maryino.district.tiik.ui.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tiik.composeapp.generated.resources.*
import maryino.district.tiik.ui.components.*
import maryino.district.tiik.ui.theme.*
import org.jetbrains.compose.resources.stringResource

// ─────────────────────────────────────────────────────────────
// Data model
// ─────────────────────────────────────────────────────────────

data class UserProfile(
    val displayName: String,
    val username: String,
    val email: String,
    val activeBlocksCount: Int,
    val friendsCount: Int,
    val heldStrongCount: Int,
    val isPro: Boolean,
    val notificationsEnabled: Boolean,
)

// ─────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(
    profile: UserProfile,
    onEditUsername: () -> Unit,
    onEditEmail: () -> Unit,
    onToggleNotifications: () -> Unit,
    onUpgradePro: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TiikColors.Bg)
            .verticalScroll(scrollState)
            .padding(bottom = Spacing.bottomNavHeight + Spacing.xl),
    ) {
        Spacer(Modifier.height(Spacing.xxl))

        // ── Avatar + name ─────────────────────────────────────
        ProfileHero(profile = profile)

        Spacer(Modifier.height(Spacing.lg))

        // ── Stats ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .padding(horizontal = Spacing.screenPadding),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            StatCell(
                value = profile.activeBlocksCount.toString(),
                label = stringResource(Res.string.profile_blocks),
                modifier = Modifier.weight(1f),
            )
            StatCell(
                value = profile.friendsCount.toString(),
                label = stringResource(Res.string.profile_friends),
                modifier = Modifier.weight(1f),
            )
            StatCell(
                value = profile.heldStrongCount.toString(),
                label = stringResource(Res.string.profile_held_strong),
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(Spacing.lg))

        // ── Pro banner (only if not subscribed) ───────────────
        if (!profile.isPro) {
            ProBanner(
                onUpgrade = onUpgradePro,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenPadding),
            )
            Spacer(Modifier.height(Spacing.lg))
        }

        // ── Account settings ──────────────────────────────────
        EyebrowText(
            text = stringResource(Res.string.profile_account),
            modifier = Modifier.padding(
                start = Spacing.screenPadding,
                bottom = Spacing.sm,
            ),
        )

        SettingsSection {
            SettingRow(
                title = stringResource(Res.string.profile_username),
                subtitle = "@${profile.username}",
                icon = { Text("👤", style = MaterialTheme.typography.bodySmall) },
                onClick = onEditUsername,
            )
            TiikDivider(Modifier.padding(start = Spacing.x4l))

            SettingRow(
                title = stringResource(Res.string.common_email_label),
                subtitle = profile.email,
                icon = { Text("📧", style = MaterialTheme.typography.bodySmall) },
                onClick = onEditEmail,
            )
            TiikDivider(Modifier.padding(start = Spacing.x4l))

            SettingRow(
                title = stringResource(Res.string.profile_notifications),
                icon = { Text("🔔", style = MaterialTheme.typography.bodySmall) },
                trailingValue = if (profile.notificationsEnabled) {
                    stringResource(Res.string.profile_on)
                } else {
                    stringResource(Res.string.profile_off)
                },
                showArrow = false,
                onClick = onToggleNotifications,
            )
        }

        Spacer(Modifier.height(Spacing.lg))

        // ── Danger zone ───────────────────────────────────────
        SettingsSection {
            SettingRow(
                title = stringResource(Res.string.profile_sign_out),
                icon = { Text("🚪", style = MaterialTheme.typography.bodySmall) },
                titleColor = TiikColors.Danger,
                showArrow = false,
                onClick = onSignOut,
            )
        }

        Spacer(Modifier.height(Spacing.x3l))

        // ── App version ───────────────────────────────────────
        Text(
            text = stringResource(Res.string.profile_version),
            style = MaterialTheme.typography.bodySmall,
            color = TiikColors.Ink3,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Profile hero
// ─────────────────────────────────────────────────────────────

@Composable
private fun ProfileHero(profile: UserProfile, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(TiikShapes.avatar)
                .background(TiikColors.BgMuted)
                .border(1.5.dp, TiikColors.Border, TiikShapes.avatar),
            contentAlignment = Alignment.Center,
        ) {
            // In production: AsyncImage with user avatar
            Text("🧑", style = MaterialTheme.typography.headlineMedium)
        }

        Spacer(Modifier.height(Spacing.md))

        Text(
            text = profile.displayName,
            style = MaterialTheme.typography.titleLarge,
            color = TiikColors.Ink,
        )
        Text(
            text = "@${profile.username}",
            style = MaterialTheme.typography.bodySmall,
            color = TiikColors.Ink3,
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Stat cell
// ─────────────────────────────────────────────────────────────

@Composable
private fun StatCell(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(TiikShapes.md)
            .background(TiikColors.BgSurface)
            .border(1.dp, TiikColors.Border, TiikShapes.md)
            .padding(vertical = Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = TiikColors.Ink,
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TiikColors.Ink3,
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Pro banner
// ─────────────────────────────────────────────────────────────

@Composable
private fun ProBanner(
    onUpgrade: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(TiikShapes.lg)
            .background(TiikColors.Ink)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(TiikShapes.sm)
                .background(TiikColors.InkOnDark.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Text("⚡", style = MaterialTheme.typography.bodyMedium)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.profile_pro_title),
                style = MaterialTheme.typography.titleSmall,
                color = TiikColors.InkOnDark,
            )
            Text(
                text = stringResource(Res.string.profile_pro_description),
                style = MaterialTheme.typography.bodySmall,
                color = TiikColors.InkOnDark.copy(alpha = 0.45f),
            )
        }

        // Upgrade CTA
        androidx.compose.material3.Surface(
            onClick = onUpgrade,
            shape = TiikShapes.pill,
            color = TiikColors.BgSurface,
        ) {
            Text(
                text = stringResource(Res.string.profile_upgrade),
                style = MaterialTheme.typography.labelSmall,
                color = TiikColors.Ink,
                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs + 1.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Settings section wrapper (white card with rounded corners)
// ─────────────────────────────────────────────────────────────

@Composable
private fun SettingsSection(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenPadding)
            .clip(TiikShapes.lg)
            .background(TiikColors.BgSurface)
            .border(1.dp, TiikColors.Border, TiikShapes.lg)
            .padding(horizontal = Spacing.md),
        content = content,
    )
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    TiikScreenPreview {
        ProfileScreen(
            profile = UserProfile(
                displayName = "Alex Petrov",
                username = "alex_focus",
                email = "alex@gmail.com",
                activeBlocksCount = 2,
                friendsCount = 3,
                heldStrongCount = 47,
                isPro = true,
                notificationsEnabled = true,
            ),
            onEditUsername = {},
            onEditEmail = {},
            onToggleNotifications = {},
            onUpgradePro = {},
            onSignOut = {},
        )
    }
}
