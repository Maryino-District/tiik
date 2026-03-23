package maryino.district.tiik.ui.features.placeholder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import maryino.district.tiik.ui.components.EyebrowText
import maryino.district.tiik.ui.theme.Spacing
import maryino.district.tiik.ui.theme.TiikColors
import maryino.district.tiik.ui.theme.TiikScreenPreview

@Composable
fun PlaceholderScreen(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TiikColors.Bg)
            .padding(horizontal = Spacing.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "\u2728",
            style = MaterialTheme.typography.displayMedium,
        )
        Spacer(Modifier.height(Spacing.xl))
        EyebrowText(text = "Placeholder")
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = TiikColors.Ink,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = TiikColors.Ink3,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun PlaceholderScreenPreview() {
    TiikScreenPreview {
        PlaceholderScreen(
            title = "Future feature slot",
            description = "Reserved for the next top-level feature.",
        )
    }
}
