package maryino.district.tiik.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import maryino.district.tiik.ui.theme.Spacing
import maryino.district.tiik.ui.theme.TiikComponentPreview
import maryino.district.tiik.ui.theme.TiikColors
import maryino.district.tiik.ui.theme.TiikShapes

@Composable
fun TiikBottomNav(
    currentRoute: String,
    onNavigate: (TiikTopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Spacing.bottomNavHeight)
            .background(TiikColors.BgSurface.copy(alpha = 0.92f))
            .padding(bottom = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        tiikTopLevelDestinations.forEach { item ->
            val isSelected = currentRoute == item.route
            NavItemView(
                item = item,
                isSelected = isSelected,
                onClick = { onNavigate(item) },
            )
        }
    }
}

@Composable
private fun NavItemView(
    item: TiikTopLevelDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = if (isSelected) TiikColors.Ink.copy(alpha = 0.06f) else Color.Transparent
    val contentColor = if (isSelected) TiikColors.Ink else TiikColors.Ink3

    Column(
        modifier = Modifier
            .clip(TiikShapes.md)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(text = item.emoji, style = MaterialTheme.typography.titleMedium)
        Text(
            text = item.label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
        )
    }
}

@Preview
@Composable
private fun TiikBottomNavPreview() {
    TiikComponentPreview {
        TiikBottomNav(
            currentRoute = BlocksDestination.route,
            onNavigate = {},
        )
    }
}
