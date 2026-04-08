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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import tiik.composeapp.generated.resources.*
import maryino.district.tiik.ui.components.EyebrowText
import maryino.district.tiik.ui.components.TiikButton
import maryino.district.tiik.ui.components.TiikButtonStyle
import maryino.district.tiik.ui.components.TiikTextField
import maryino.district.tiik.ui.resources.UiText
import maryino.district.tiik.ui.resources.asString
import maryino.district.tiik.ui.theme.Spacing
import maryino.district.tiik.ui.theme.TiikColors
import maryino.district.tiik.ui.theme.TiikScreenPreview
import org.jetbrains.compose.resources.stringResource

@Composable
fun ForgotPasswordScreen(
    initialEmail: String,
    onBackToSignIn: () -> Unit,
    onCreateNewPassword: (String) -> Unit,
    onShowMessage: (String) -> Unit,
    requestPasswordReset: suspend (String) -> Unit,
    checkResetPasswordStatus: suspend (String) -> ForgotPasswordResetStatusResult,
    modifier: Modifier = Modifier,
    forgotPasswordViewModel: ForgotPasswordViewModel = viewModel {
        ForgotPasswordViewModel(
            initialEmail = initialEmail,
            requestPasswordReset = requestPasswordReset,
            checkResetPasswordStatus = checkResetPasswordStatus,
        )
    },
) {
    val state by forgotPasswordViewModel.uiState.collectAsStateWithLifecycle()
    var pendingMessage by remember { mutableStateOf<UiText?>(null) }
    val pendingMessageText = pendingMessage?.asString()

    LaunchedEffect(forgotPasswordViewModel, onBackToSignIn, onCreateNewPassword, onShowMessage) {
        forgotPasswordViewModel.effects.collectLatest { effect ->
            when (effect) {
                ForgotPasswordEffect.BackToSignIn -> onBackToSignIn()
                is ForgotPasswordEffect.NavigateToCreateNewPassword -> onCreateNewPassword(effect.email)
                is ForgotPasswordEffect.ShowMessage -> pendingMessage = effect.message
            }
        }
    }

    LaunchedEffect(pendingMessageText, onShowMessage) {
        pendingMessageText?.let { message ->
            onShowMessage(message)
            pendingMessage = null
        }
    }

    ForgotPasswordScreenContent(
        state = state,
        onIntent = forgotPasswordViewModel::onIntent,
        onBackToSignIn = onBackToSignIn,
        modifier = modifier,
    )
}

@Composable
private fun ForgotPasswordScreenContent(
    state: ForgotPasswordState,
    onIntent: (ForgotPasswordIntent) -> Unit,
    onBackToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TiikColors.Bg)
            .padding(horizontal = Spacing.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AuthTopBar(
            onBackClick = onBackToSignIn,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.xl))

        TiikLogoMark()

        Spacer(Modifier.height(Spacing.lg))

        EyebrowText(
            text = stringResource(Res.string.auth_reset_password),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(Spacing.xl))

        if (state.isResetEmailSent) {
            Text(
                text = stringResource(Res.string.auth_forgot_password_success, state.email),
                style = MaterialTheme.typography.bodyMedium,
                color = TiikColors.Ink2,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.xl))

            TiikButton(
                text = if (state.isCheckingResetStatus) {
                    stringResource(Res.string.auth_checking)
                } else {
                    stringResource(Res.string.auth_check_reset_email_status)
                },
                onClick = { onIntent(ForgotPasswordIntent.ContinueClicked) },
                enabled = state.isContinueEnabled,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.md))

            TiikButton(
                text = stringResource(Res.string.auth_back_to_sign_in),
                onClick = { onIntent(ForgotPasswordIntent.BackToSignInClicked) },
                style = TiikButtonStyle.Ghost,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            TiikTextField(
                value = state.email,
                onValueChange = { onIntent(ForgotPasswordIntent.EmailChanged(it)) },
                placeholder = stringResource(Res.string.common_email_placeholder),
                label = stringResource(Res.string.common_email_label),
                modifier = Modifier.fillMaxWidth(),
            )

            if (state.validationMessage != null) {
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = state.validationMessage.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = TiikColors.Danger,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(Spacing.xl))

            TiikButton(
                text = if (state.isSubmitting) {
                    stringResource(Res.string.auth_sending)
                } else {
                    stringResource(Res.string.auth_send_reset_link)
                },
                onClick = { onIntent(ForgotPasswordIntent.SubmitClicked) },
                enabled = state.isSubmitEnabled,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.md))

            TiikButton(
                text = stringResource(Res.string.auth_back_to_sign_in),
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
            onCreateNewPassword = {},
            onShowMessage = {},
            requestPasswordReset = {},
            checkResetPasswordStatus = { ForgotPasswordResetStatusResult.AwaitingConfirmation },
        )
    }
}
