package maryino.district.tiik.ui.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import maryino.district.tiik.ui.components.EyebrowText
import maryino.district.tiik.ui.components.TiikButton
import maryino.district.tiik.ui.components.TiikButtonStyle
import maryino.district.tiik.ui.components.TiikTextField
import maryino.district.tiik.ui.theme.Spacing
import maryino.district.tiik.ui.theme.TiikColors
import maryino.district.tiik.ui.theme.TiikScreenPreview

@Composable
fun ForgotPasswordScreen(
    initialEmail: String,
    onBackToSignIn: () -> Unit,
    requestPasswordReset: suspend (String) -> Unit,
    modifier: Modifier = Modifier,
    forgotPasswordViewModel: ForgotPasswordViewModel = viewModel {
        ForgotPasswordViewModel(
            initialEmail = initialEmail,
            requestPasswordReset = requestPasswordReset,
        )
    },
) {
    val state by forgotPasswordViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(forgotPasswordViewModel, onBackToSignIn) {
        forgotPasswordViewModel.effects.collectLatest { effect ->
            when (effect) {
                ForgotPasswordEffect.BackToSignIn -> onBackToSignIn()
            }
        }
    }

    ForgotPasswordScreenContent(
        state = state,
        onIntent = forgotPasswordViewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
private fun ForgotPasswordScreenContent(
    state: ForgotPasswordState,
    onIntent: (ForgotPasswordIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TiikColors.Bg)
            .padding(horizontal = Spacing.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(Spacing.x5l))

        TiikLogoMark()

        Spacer(Modifier.height(Spacing.lg))

        EyebrowText(
            text = "Reset password",
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(Spacing.xl))

        if (state.isSuccess) {
            Text(
                text = FORGOT_PASSWORD_SUCCESS_MESSAGE,
                style = MaterialTheme.typography.bodyMedium,
                color = TiikColors.Ink2,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.xl))

            TiikButton(
                text = "Back to sign in",
                onClick = { onIntent(ForgotPasswordIntent.BackToSignInClicked) },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            TiikTextField(
                value = state.email,
                onValueChange = { onIntent(ForgotPasswordIntent.EmailChanged(it)) },
                placeholder = "you@example.com",
                label = "Email",
                modifier = Modifier.fillMaxWidth(),
            )

            if (state.validationMessage != null) {
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = state.validationMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = TiikColors.Danger,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(Spacing.xl))

            TiikButton(
                text = if (state.isSubmitting) "Sending..." else "Send reset link",
                onClick = { onIntent(ForgotPasswordIntent.SubmitClicked) },
                enabled = state.isSubmitEnabled,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.md))

            TiikButton(
                text = "Back to sign in",
                onClick = { onIntent(ForgotPasswordIntent.BackToSignInClicked) },
                style = TiikButtonStyle.Ghost,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun ForgotPasswordScreenPreview() {
    TiikScreenPreview {
        ForgotPasswordScreen(
            initialEmail = "user@example.com",
            onBackToSignIn = {},
            requestPasswordReset = {},
        )
    }
}
