package maryino.district.tiik

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import maryino.district.tiik.ui.navigation.TiikApp

@Composable
@Preview
fun App() {
    TiikApp(
        isOnboardingComplete = false,
        isLoggedIn = false,
    )
}
