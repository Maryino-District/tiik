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
import maryino.district.tiik.ui.resources.asString
import maryino.district.tiik.ui.theme.Spacing
import maryino.district.tiik.ui.theme.TiikColors
import maryino.district.tiik.ui.theme.TiikScreenPreview
import org.jetbrains.compose.resources.stringResource
import tiik.composeapp.generated.resources.Res
import tiik.composeapp.generated.resources.auth_checking
import tiik.composeapp.generated.resources.auth_create_new_password
import tiik.composeapp.generated.resources.auth_create_new_password_body

@Composable
fun CreateNewPasswordScreen(
    initialEmail: String,
    onPasswordResetCompleted: () -> Unit,
    onBack: () -> Unit,
    updatePassword: suspend (String, String) -> Unit,
    modifier: Modifier = Modifier,
    createNewPasswordViewModel: CreateNewPasswordViewModel = viewModel {
        CreateNewPasswordViewModel(
            initialEmail = initialEmail,
            updatePassword = updatePassword,
        )
    },
) {
    val state by createNewPasswordViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(createNewPasswordViewModel, onPasswordResetCompleted) {
        createNewPasswordViewModel.effects.collectLatest { effect ->
            when (effect) {
                CreateNewPasswordEffect.PasswordResetCompleted -> onPasswordResetCompleted()
            }
        }
    }

    CreateNewPasswordScreenContent(
        state = state,
        onIntent = createNewPasswordViewModel::onIntent,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
private fun CreateNewPasswordScreenContent(
    state: CreateNewPasswordState,
    onIntent: (CreateNewPasswordIntent) -> Unit,
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
            text = stringResource(Res.string.auth_create_new_password),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(Spacing.xl))

        Text(
            text = stringResource(Res.string.auth_create_new_password_body, state.email),
            style = MaterialTheme.typography.bodyMedium,
            color = TiikColors.Ink2,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.xl))

        AuthPasswordFields(
            password = state.password,
            repeatPassword = state.repeatPassword,
            onPasswordChanged = { onIntent(CreateNewPasswordIntent.PasswordChanged(it)) },
            onRepeatPasswordChanged = { onIntent(CreateNewPasswordIntent.RepeatPasswordChanged(it)) },
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
                stringResource(Res.string.auth_checking)
            } else {
                stringResource(Res.string.auth_create_new_password)
            },
            onClick = { onIntent(CreateNewPasswordIntent.SubmitClicked) },
            enabled = state.isSubmitEnabled,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun CreateNewPasswordScreenPreview() {
    TiikScreenPreview {
        CreateNewPasswordScreen(
            initialEmail = "user@example.com",
            onPasswordResetCompleted = {},
            onBack = {},
            updatePassword = { _, _ -> },
        )
    }
}
