package maryino.district.tiik.ui.features.addblock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
// Data models
// ─────────────────────────────────────────────────────────────

data class InstalledApp(
    val packageName: String,
    val label: String,
    // In production: use Painter from PackageManager
)

data class FriendUser(
    val id: String,
    val username: String,
    val displayName: String,
)

// ─────────────────────────────────────────────────────────────
// Screen — multi-step container
// ─────────────────────────────────────────────────────────────

@Composable
fun AddBlockScreen(
    installedApps: List<InstalledApp>,
    friends: List<FriendUser>,
    onDismiss: () -> Unit,
    onBlockCreated: (app: InstalledApp, guardian: FriendUser) -> Unit,
    modifier: Modifier = Modifier,
) {
    var step             by remember { mutableStateOf(0) }
    var selectedApp      by remember { mutableStateOf<InstalledApp?>(null) }
    var selectedGuardian by remember { mutableStateOf<FriendUser?>(null) }
    var searchQuery      by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TiikColors.Bg),
    ) {
        // ── Shared top bar ────────────────────────────────────
        AddBlockTopBar(
            step = step,
            totalSteps = 3,
            onBack = {
                if (step == 0) onDismiss()
                else step--
            },
        )

        // ── Step content ──────────────────────────────────────
        when (step) {
            0 -> StepChooseApp(
                apps = installedApps,
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                selectedApp = selectedApp,
                onSelect = { selectedApp = it },
                onNext = { if (selectedApp != null) step = 1 },
            )
            1 -> StepChooseGuardian(
                friends = friends,
                selectedGuardian = selectedGuardian,
                onSelect = { selectedGuardian = it },
                onNext = { if (selectedGuardian != null) step = 2 },
            )
            2 -> StepConfirm(
                app = selectedApp!!,
                guardian = selectedGuardian!!,
                onConfirm = { onBlockCreated(selectedApp!!, selectedGuardian!!) },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Shared top bar
// ─────────────────────────────────────────────────────────────

@Composable
private fun AddBlockTopBar(
    step: Int,
    totalSteps: Int,
    onBack: () -> Unit,
) {
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

        Column(modifier = Modifier.weight(1f)) {
            EyebrowText(stringResource(Res.string.add_block_step_title, step + 1))
            Spacer(Modifier.height(2.dp))
            Text(
                text = when (step) {
                    0 -> stringResource(Res.string.add_block_choose_app)
                    1 -> stringResource(Res.string.add_block_pick_guardian)
                    else -> stringResource(Res.string.add_block_confirm)
                },
                style = MaterialTheme.typography.headlineSmall,
                color = TiikColors.Ink,
            )
        }
    }

    StepDots(
        totalSteps = totalSteps,
        currentStep = step,
        modifier = Modifier.padding(
            start = Spacing.screenPadding,
            bottom = Spacing.lg,
        ),
    )
}

// ─────────────────────────────────────────────────────────────
// Step 1: Choose app
// ─────────────────────────────────────────────────────────────

@Composable
private fun StepChooseApp(
    apps: List<InstalledApp>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedApp: InstalledApp?,
    onSelect: (InstalledApp) -> Unit,
    onNext: () -> Unit,
) {
    val filtered = remember(searchQuery, apps) {
        if (searchQuery.isBlank()) apps
        else apps.filter { it.label.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        TiikTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = stringResource(Res.string.add_block_search_apps),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenPadding),
        )

        Spacer(Modifier.height(Spacing.lg))

        EyebrowText(
            text = stringResource(Res.string.add_block_installed, filtered.size),
            modifier = Modifier.padding(
                start = Spacing.screenPadding,
                bottom = Spacing.sm,
            ),
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = Spacing.screenPadding),
        ) {
            items(filtered, key = { it.packageName }) { app ->
                val isSelected = selectedApp?.packageName == app.packageName

                AppRow(
                    appName = app.label,
                    appIcon = { Text("📱", style = MaterialTheme.typography.bodyLarge) },
                    modifier = Modifier.clickable { onSelect(app) },
                    trailing = {
                        // Checkbox
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(TiikShapes.xs)
                                .background(if (isSelected) TiikColors.Ink else TiikColors.BgSurface)
                                .border(
                                    1.5.dp,
                                    if (isSelected) TiikColors.Ink else TiikColors.Border,
                                    TiikShapes.xs,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isSelected) {
                                Text(
                                    text = "✓",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TiikColors.InkOnDark,
                                )
                            }
                        }
                    },
                )

                TiikDivider()
            }
        }

        // Bottom CTA
        Column(
            modifier = Modifier.padding(
                horizontal = Spacing.screenPadding,
                vertical = Spacing.lg,
            ),
        ) {
            if (selectedApp != null) {
                Text(
                    text = stringResource(Res.string.add_block_selected_count),
                    style = MaterialTheme.typography.bodySmall,
                    color = TiikColors.Ink3,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = Spacing.sm),
                )
            }
            TiikButton(
                text = stringResource(Res.string.add_block_next_pick_guardian),
                onClick = onNext,
                enabled = selectedApp != null,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Step 2: Choose guardian
// ─────────────────────────────────────────────────────────────

@Composable
private fun StepChooseGuardian(
    friends: List<FriendUser>,
    selectedGuardian: FriendUser?,
    onSelect: (FriendUser) -> Unit,
    onNext: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        EyebrowText(
            text = stringResource(Res.string.add_block_friends, friends.size),
            modifier = Modifier.padding(
                start = Spacing.screenPadding,
                bottom = Spacing.sm,
            ),
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = Spacing.screenPadding),
        ) {
            items(friends, key = { it.id }) { friend ->
                val isSelected = selectedGuardian?.id == friend.id

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clickable { onSelect(friend) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(TiikShapes.avatar)
                            .background(TiikColors.BgMuted)
                            .border(1.5.dp, TiikColors.Border, TiikShapes.avatar),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("😎", style = MaterialTheme.typography.bodyLarge)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = friend.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            color = TiikColors.Ink,
                        )
                        Text(
                            text = "@${friend.username}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TiikColors.Ink3,
                        )
                    }

                    // Radio button
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(TiikShapes.avatar)
                            .background(if (isSelected) TiikColors.Ink else TiikColors.BgSurface)
                            .border(
                                1.5.dp,
                                if (isSelected) TiikColors.Ink else TiikColors.Border,
                                TiikShapes.avatar,
                            ),
                    )
                }

                TiikDivider()
            }
        }

        TiikButton(
            text = stringResource(Res.string.add_block_next_confirm),
            onClick = onNext,
            enabled = selectedGuardian != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenPadding, vertical = Spacing.lg),
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Step 3: Confirm
// ─────────────────────────────────────────────────────────────

@Composable
private fun StepConfirm(
    app: InstalledApp,
    guardian: FriendUser,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.add_block_about_to_block),
            style = MaterialTheme.typography.bodyMedium,
            color = TiikColors.Ink3,
        )

        // App card
        TiikCard(highlighted = true, modifier = Modifier.fillMaxWidth()) {
            AppRow(
                appName = app.label,
                appIcon = { Text("📱", style = MaterialTheme.typography.bodyLarge) },
            )
        }

        Text(
            text = stringResource(Res.string.add_block_guardian_will_be),
            style = MaterialTheme.typography.bodyMedium,
            color = TiikColors.Ink3,
        )

        // Guardian card
        GuardianRow(username = "@${guardian.username}")

        // Info note
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(TiikShapes.md)
                .background(TiikColors.BgMuted)
                .padding(Spacing.md),
        ) {
            Text(
                text = stringResource(
                    Res.string.add_block_guardian_note,
                    guardian.username,
                    app.label,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = TiikColors.Ink3,
            )
        }

        Spacer(Modifier.weight(1f))

        TiikButton(
            text = stringResource(Res.string.add_block_lock_app, app.label),
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.x3l),
        )
    }
}

@Preview
@Composable
private fun AddBlockScreenPreview() {
    TiikScreenPreview {
        AddBlockScreen(
            installedApps = listOf(
                InstalledApp("com.instagram.android", "Instagram"),
                InstalledApp("com.reddit.frontpage", "Reddit"),
                InstalledApp("com.google.android.youtube", "YouTube"),
            ),
            friends = listOf(
                FriendUser("1", "maksim_k", "Maksim K."),
                FriendUser("2", "anna_dev", "Anna Dev"),
            ),
            onDismiss = {},
            onBlockCreated = { _, _ -> },
        )
    }
}
