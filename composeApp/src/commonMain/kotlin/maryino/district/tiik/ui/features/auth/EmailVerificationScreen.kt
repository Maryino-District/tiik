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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import tiik.composeapp.generated.resources.*
import maryino.district.tiik.ui.components.EyebrowText
import maryino.district.tiik.ui.components.TiikButton
import maryino.district.tiik.ui.components.TiikButtonStyle
import maryino.district.tiik.ui.resources.asString
import maryino.district.tiik.ui.theme.Spacing
import maryino.district.tiik.ui.theme.TiikColors
import maryino.district.tiik.ui.theme.TiikScreenPreview
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmailVerificationScreen(
    initialEmail: String,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    resendVerificationEmail: suspend (String) -> Unit,
    modifier: Modifier = Modifier,
    emailVerificationViewModel: EmailVerificationViewModel = viewModel {
        EmailVerificationViewModel(
            initialEmail = initialEmail,
            resendVerificationEmail = resendVerificationEmail,
        )
    },
) {
    val state by emailVerificationViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(emailVerificationViewModel, onContinue) {
        emailVerificationViewModel.effects.collectLatest { effect ->
            when (effect) {
                EmailVerificationEffect.Continue -> onContinue()
            }
        }
    }

    EmailVerificationScreenContent(
        state = state,
        onIntent = emailVerificationViewModel::onIntent,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
private fun EmailVerificationScreenContent(
    state: EmailVerificationState,
    onIntent: (EmailVerificationIntent) -> Unit,
    onBack: () -> Unit,
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
            onBackClick = onBack,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.xl))

        TiikLogoMark()

        Spacer(Modifier.height(Spacing.lg))

        EyebrowText(
            text = stringResource(Res.string.auth_verify_email),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(Spacing.xl))

        Text(
            text = stringResource(Res.string.auth_verify_email_body, state.email),
            style = MaterialTheme.typography.bodyMedium,
            color = TiikColors.Ink2,
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.resendConfirmationMessage != null) {
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = state.resendConfirmationMessage.asString(),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = TiikColors.Ink,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(Spacing.xl))

        TiikButton(
            text = stringResource(Res.string.auth_verified_email),
            onClick = { onIntent(EmailVerificationIntent.ContinueClicked) },
            enabled = state.isContinueEnabled,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.md))

        TiikButton(
            text = if (state.isResending) {
                stringResource(Res.string.auth_sending)
            } else {
                stringResource(Res.string.auth_resend_email)
            },
            onClick = { onIntent(EmailVerificationIntent.ResendClicked) },
            enabled = state.isResendEnabled,
            style = TiikButtonStyle.Ghost,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun EmailVerificationScreenPreview() {
    TiikScreenPreview {
        EmailVerificationScreen(
            initialEmail = "user@example.com",
            onContinue = {},
            onBack = {},
            resendVerificationEmail = {},
        )
    }
}
