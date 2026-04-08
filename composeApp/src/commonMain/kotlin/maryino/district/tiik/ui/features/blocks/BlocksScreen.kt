package maryino.district.tiik.ui.features.blocks

import androidx.compose.foundation.background
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

data class BlockItem(
    val id: String,
    val appName: String,
    val appPackage: String,
    val guardianUsername: String,
    val status: BlockStatus,
)

enum class BlockStatus { Locked, Pending, Unlocked }

data class GuardianRequest(
    val id: String,
    val requesterName: String,
    val appName: String,
    val message: String?,
)

// ─────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────

@Composable
fun BlocksScreen(
    myBlocks: List<BlockItem>,
    guardianRequests: List<GuardianRequest>,
    onAddBlock: () -> Unit,
    onBlockClick: (BlockItem) -> Unit,
    onApproveRequest: (GuardianRequest) -> Unit,
    onDenyRequest: (GuardianRequest) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TiikColors.Bg),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Spacing.screenPadding,
                        end = Spacing.screenPadding,
                        top = Spacing.xxl,
                        bottom = Spacing.md,
                    )
            ) {
                EyebrowText("tiik")
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = stringResource(Res.string.blocks_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = TiikColors.Ink,
                )
            }

            // ── Tab row ───────────────────────────────────────
            Row(
                modifier = Modifier
                    .padding(horizontal = Spacing.screenPadding)
                    .padding(bottom = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                TiikChip(
                    text = stringResource(Res.string.blocks_tab_my_blocks),
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                )
                TiikChip(
                    text = if (guardianRequests.isEmpty()) {
                        stringResource(Res.string.blocks_tab_guardian)
                    } else {
                        stringResource(Res.string.blocks_tab_guardian_count, guardianRequests.size)
                    },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                )
            }

            // ── Content ───────────────────────────────────────
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = Spacing.screenPadding,
                    end = Spacing.screenPadding,
                    bottom = Spacing.bottomNavHeight + Spacing.xl,
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                if (selectedTab == 0) {
                    // ── My blocks tab ─────────────────────────
                    val active  = myBlocks.filter { it.status != BlockStatus.Unlocked }
                    val history = myBlocks.filter { it.status == BlockStatus.Unlocked }

                    if (active.isNotEmpty()) {
                        item {
                            EyebrowText(
                                stringResource(Res.string.blocks_active, active.size),
                                modifier = Modifier.padding(bottom = Spacing.xs),
                            )
                        }
                        items(active, key = { it.id }) { block ->
                            BlockCard(
                                block = block,
                                onClick = { onBlockClick(block) },
                            )
                        }
                    }

                    if (active.isEmpty()) {
                        item { BlocksEmptyState(onAddBlock = onAddBlock) }
                    }

                    if (history.isNotEmpty()) {
                        item {
                            TiikDivider(Modifier.padding(vertical = Spacing.sm))
                            EyebrowText(
                                stringResource(Res.string.blocks_recent),
                                modifier = Modifier.padding(bottom = Spacing.xs),
                            )
                        }
                        items(history, key = { it.id }) { block ->
                            BlockCard(block = block, onClick = null, dimmed = true)
                        }
                    }

                } else {
                    // ── Guardian tab ──────────────────────────
                    if (guardianRequests.isEmpty()) {
                        item { GuardianEmptyState() }
                    } else {
                        item {
                            EyebrowText(
                                stringResource(Res.string.blocks_needs_approval),
                                modifier = Modifier.padding(bottom = Spacing.xs),
                            )
                        }
                        items(guardianRequests, key = { it.id }) { req ->
                            GuardianRequestCard(
                                request = req,
                                onApprove = { onApproveRequest(req) },
                                onDeny = { onDenyRequest(req) },
                            )
                        }
                    }
                }
            }
        }

        // ── FAB ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = Spacing.screenPadding,
                    bottom = Spacing.bottomNavHeight + Spacing.lg,
                )
        ) {
            TiikFab(onClick = onAddBlock)
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Block card
// ─────────────────────────────────────────────────────────────

@Composable
private fun BlockCard(
    block: BlockItem,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    dimmed: Boolean = false,
) {
    val highlighted = block.status == BlockStatus.Locked

    TiikCard(
        highlighted = highlighted,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .then(if (dimmed) Modifier else Modifier),
    ) {
        AppRow(
            appName = block.appName,
            subtitle = stringResource(Res.string.blocks_guardian_subtitle, block.guardianUsername),
            appIcon = {
                // In production: AsyncImage with PackageManager icon
                Text("📱", style = MaterialTheme.typography.bodyLarge)
            },
            trailing = {
                val badgeStyle = when (block.status) {
                    BlockStatus.Locked   -> TiikBadgeStyle.Dark
                    BlockStatus.Pending  -> TiikBadgeStyle.Light
                    BlockStatus.Unlocked -> TiikBadgeStyle.Light
                }
                val badgeText = when (block.status) {
                    BlockStatus.Locked -> stringResource(Res.string.blocks_status_locked)
                    BlockStatus.Pending -> stringResource(Res.string.blocks_status_pending)
                    BlockStatus.Unlocked -> stringResource(Res.string.blocks_status_unlocked)
                }
                TiikBadge(text = badgeText, style = badgeStyle)
            },
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Guardian request card
// ─────────────────────────────────────────────────────────────

@Composable
private fun GuardianRequestCard(
    request: GuardianRequest,
    onApprove: () -> Unit,
    onDeny: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TiikCard(
        highlighted = true,
        modifier = modifier.fillMaxWidth(),
    ) {
        EyebrowText(
            text = stringResource(Res.string.blocks_from_guardian, request.requesterName),
            modifier = Modifier.padding(bottom = Spacing.sm),
        )

        AppRow(
            appName = request.appName,
            appIcon = { Text("📱", style = MaterialTheme.typography.bodyLarge) },
            subtitle = stringResource(Res.string.blocks_wants_to_unlock),
        )

        if (!request.message.isNullOrBlank()) {
            Spacer(Modifier.height(Spacing.sm))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(TiikShapes.sm)
                    .background(TiikColors.BgMuted)
                    .padding(Spacing.sm),
            ) {
                Text(
                    text = "\"${request.message}\"",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    color = TiikColors.Ink2,
                )
            }
        }

        Spacer(Modifier.height(Spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            TiikButton(
                text = stringResource(Res.string.blocks_deny),
                onClick = onDeny,
                style = TiikButtonStyle.Ghost,
                modifier = Modifier.weight(1f),
            )
            TiikButton(
                text = stringResource(Res.string.blocks_approve),
                onClick = onApprove,
                style = TiikButtonStyle.Fill,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// FAB
// ─────────────────────────────────────────────────────────────

@Composable
fun TiikFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(52.dp)
            .clip(TiikShapes.md)
            .background(TiikColors.Ink),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.material3.Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            color = TiikColors.Ink,
            shape = TiikShapes.md,
            shadowElevation = 4.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TiikColors.InkOnDark,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Empty states
// ─────────────────────────────────────────────────────────────

@Composable
private fun BlocksEmptyState(
    onAddBlock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.x4l),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // Mascot placeholder — replace with actual Image(painterResource("mascot.xml"))
        Text("🐦", style = MaterialTheme.typography.displayMedium)

        Text(
            text = stringResource(Res.string.blocks_empty_title),
            style = MaterialTheme.typography.titleLarge,
            color = TiikColors.Ink,
        )
        Text(
            text = stringResource(Res.string.blocks_empty_description),
            style = MaterialTheme.typography.bodyMedium,
            color = TiikColors.Ink3,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )

        Spacer(Modifier.height(Spacing.sm))

        TiikButton(
            text = stringResource(Res.string.blocks_add_first),
            onClick = onAddBlock,
            modifier = Modifier.width(200.dp),
        )
    }
}

@Composable
private fun GuardianEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.x4l),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text("👀", style = MaterialTheme.typography.displayMedium)
        Text(
            text = stringResource(Res.string.blocks_guardian_empty_title),
            style = MaterialTheme.typography.titleLarge,
            color = TiikColors.Ink,
        )
        Text(
            text = stringResource(Res.string.blocks_guardian_empty_description),
            style = MaterialTheme.typography.bodyMedium,
            color = TiikColors.Ink3,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun BlocksScreenPreview() {
    TiikScreenPreview {
        BlocksScreen(
            myBlocks = listOf(
                BlockItem("1", "Instagram", "com.instagram.android", "maksim_k", BlockStatus.Locked),
                BlockItem("2", "TikTok", "com.zhiliaoapp.musically", "anna_dev", BlockStatus.Pending),
            ),
            guardianRequests = listOf(
                GuardianRequest("r1", "dima_work", "YouTube", "Need it for work"),
            ),
            onAddBlock = {},
            onBlockClick = {},
            onApproveRequest = {},
            onDenyRequest = {},
        )
    }
}
