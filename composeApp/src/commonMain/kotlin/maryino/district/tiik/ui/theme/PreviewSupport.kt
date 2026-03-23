package maryino.district.tiik.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TiikComponentPreview(content: @Composable () -> Unit) {
    TiikTheme {
        Box(
            modifier = Modifier
                .background(TiikColors.Bg)
                .padding(Spacing.lg),
        ) {
            content()
        }
    }
}

@Composable
fun TiikScreenPreview(content: @Composable () -> Unit) {
    TiikTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TiikColors.Bg),
        ) {
            content()
        }
    }
}
