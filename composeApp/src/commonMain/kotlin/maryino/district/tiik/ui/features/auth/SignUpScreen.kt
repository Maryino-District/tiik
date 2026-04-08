package maryino.district.tiik.ui.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import maryino.district.tiik.ui.components.TiikTextField
import maryino.district.tiik.ui.resources.UiText
import maryino.district.tiik.ui.resources.asString
import maryino.district.tiik.ui.theme.Spacing
import maryino.district.tiik.ui.theme.TiikColors
import maryino.district.tiik.ui.theme.TiikScreenPreview
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignUpScreen(
    onSignUp: (email: String, password: String) -> Unit,
    onBack: () -> Unit,
    onForgotPassword: (email: String) -> Unit,
    checkEmailAvailability: suspend (String) -> SignUpEmailCheckResult,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    signUpViewModel: SignUpViewModel = viewModel {
        SignUpViewModel(checkEmailAvailability = checkEmailAvailability)
    },
) {
    val state by signUpViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(signUpViewModel, onSignUp, onForgotPassword) {
        signUpViewModel.effects.collectLatest { effect ->
            when (effect) {
                is SignUpEffect.SignUp -> onSignUp(effect.email, effect.password)
                is SignUpEffect.ForgotPassword -> onForgotPassword(effect.email)
            }
        }
    }

    SignUpScreenContent(
        state = state,
        onIntent = signUpViewModel::onIntent,
        onBack = onBack,
        modifier = modifier,
        isLoading = isLoading,
    )
}

@Composable
private fun SignUpScreenContent(
    state: SignUpState,
    onIntent: (SignUpIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
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
            text = stringResource(Res.string.auth_create_account),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(Spacing.xl))

        TiikTextField(
            value = state.email,
            onValueChange = { onIntent(SignUpIntent.EmailChanged(it)) },
            placeholder = stringResource(Res.string.common_email_placeholder),
            label = stringResource(Res.string.common_email_label),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.sm))

        TiikTextField(
            value = state.password,
            onValueChange = { onIntent(SignUpIntent.PasswordChanged(it)) },
            placeholder = stringResource(Res.string.common_password_placeholder),
            label = stringResource(Res.string.common_password_label),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.sm))

        TiikTextField(
            value = state.repeatPassword,
            onValueChange = { onIntent(SignUpIntent.RepeatPasswordChanged(it)) },
            placeholder = stringResource(Res.string.common_password_placeholder),
            label = stringResource(Res.string.auth_repeat_password),
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
            text = if (state.isCheckingEmail || isLoading) {
                stringResource(Res.string.auth_checking)
            } else {
                stringResource(Res.string.auth_create_account)
            },
            onClick = { onIntent(SignUpIntent.SubmitClicked) },
            enabled = state.isSubmitEnabled && !isLoading,
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.validationMessage == UiText.from(Res.string.auth_sign_up_account_exists)) {
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = stringResource(Res.string.auth_go_to_forgot_password),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = TiikColors.Ink,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onIntent(SignUpIntent.ForgotPasswordClicked) },
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = stringResource(Res.string.auth_already_have_account),
            style = MaterialTheme.typography.bodySmall,
            color = TiikColors.Ink3,
            modifier = Modifier.padding(bottom = Spacing.x3l),
        )
    }
}

@Preview
@Composable
private fun SignUpScreenPreview() {
    TiikScreenPreview {
        SignUpScreen(
            onSignUp = { _, _ -> },
            onBack = {},
            onForgotPassword = {},
            checkEmailAvailability = { SignUpEmailCheckResult.Available },
        )
    }
}
