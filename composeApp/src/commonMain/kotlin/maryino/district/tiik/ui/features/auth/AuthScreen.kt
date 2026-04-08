package maryino.district.tiik.ui.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import tiik.composeapp.generated.resources.*
import maryino.district.tiik.ui.components.*
import maryino.district.tiik.ui.theme.*
import org.jetbrains.compose.resources.stringResource

// ─────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────

@Composable
fun AuthScreen(
    onSignIn: (email: String, password: String) -> Unit,
    onSignUpClick: () -> Unit,
    onGoogleAuth: () -> Unit,
    onForgotPassword: (email: String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    authViewModel: AuthViewModel = viewModel { AuthViewModel() },
) {
    val state by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(authViewModel, onSignIn, onGoogleAuth, onForgotPassword) {
        authViewModel.effects.collectLatest { effect ->
            when (effect) {
                is AuthEffect.SignIn -> onSignIn(effect.email, effect.password)
                AuthEffect.GoogleAuth -> onGoogleAuth()
                is AuthEffect.ForgotPassword -> onForgotPassword(effect.email)
            }
        }
    }

    AuthScreenContent(
        state = state,
        onIntent = authViewModel::onIntent,
        onSignUpClick = onSignUpClick,
        modifier = modifier,
        isLoading = isLoading,
    )
}

@Composable
private fun AuthScreenContent(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
    onSignUpClick: () -> Unit,
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
        Spacer(Modifier.height(Spacing.x5l))

        // ── Logo ─────────────────────────────────────────────
        TiikLogoMark()

        Spacer(Modifier.height(Spacing.lg))

        EyebrowText(
            text = stringResource(Res.string.auth_welcome_back),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(Spacing.xl))

        // ── Form ─────────────────────────────────────────────
        TiikTextField(
            value = state.email,
            onValueChange = { onIntent(AuthIntent.EmailChanged(it)) },
            placeholder = stringResource(Res.string.common_email_placeholder),
            label = stringResource(Res.string.common_email_label),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.sm))

        TiikTextField(
            value = state.password,
            onValueChange = { onIntent(AuthIntent.PasswordChanged(it)) },
            placeholder = stringResource(Res.string.common_password_placeholder),
            label = stringResource(Res.string.common_password_label),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = stringResource(Res.string.auth_forgot_password),
            style = MaterialTheme.typography.bodySmall,
            color = TiikColors.Ink3,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onIntent(AuthIntent.ForgotPasswordClicked) },
        )

        Spacer(Modifier.height(Spacing.xl))

        TiikButton(
            text = stringResource(Res.string.auth_sign_in),
            onClick = { onIntent(AuthIntent.SubmitClicked) },
            enabled = state.isSubmitEnabled && !isLoading,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.lg))

        // ── Divider ───────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            TiikDivider(Modifier.weight(1f))
            Text(stringResource(Res.string.auth_or), style = MaterialTheme.typography.bodySmall, color = TiikColors.Ink3)
            TiikDivider(Modifier.weight(1f))
        }

        Spacer(Modifier.height(Spacing.lg))

        // ── Google ────────────────────────────────────────────
        TiikButton(
            text = stringResource(Res.string.auth_continue_with_google),
            onClick = { onIntent(AuthIntent.GoogleAuthClicked) },
            style = TiikButtonStyle.Light,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.weight(1f))

        val toggleText = buildAnnotatedString {
            append(stringResource(Res.string.auth_no_account))
            withStyle(SpanStyle(color = TiikColors.Ink, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)) {
                append(stringResource(Res.string.auth_sign_up))
            }
        }
        Text(
            text = toggleText,
            style = MaterialTheme.typography.bodySmall,
            color = TiikColors.Ink3,
            modifier = Modifier
                .padding(bottom = Spacing.x3l)
                .clickable(onClick = onSignUpClick),
        )
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AuthTopBar(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val backDescription = stringResource(Res.string.common_back)

    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Text(
                    text = "\u2190",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TiikColors.Ink,
                    modifier = Modifier.semantics { contentDescription = backDescription },
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = TiikColors.Bg,
            navigationIconContentColor = TiikColors.Ink,
        ),
        modifier = modifier,
    )
}

// ─────────────────────────────────────────────────────────────
// Logo mark
// ─────────────────────────────────────────────────────────────

@Composable
fun TiikLogoMark(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(TiikShapes.md)
                .background(TiikColors.Ink),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.auth_logo_mark),
                style = MaterialTheme.typography.headlineMedium,
                color = TiikColors.InkOnDark,
            )
        }
        // Wordmark
        Text(
            text = stringResource(Res.string.auth_logo_wordmark),
            style = MaterialTheme.typography.headlineSmall,
            color = TiikColors.Ink,
        )
    }
}

@Preview
@Composable
private fun AuthScreenPreview() {
    TiikScreenPreview {
        AuthScreen(
            onSignIn = { _, _ -> },
            onSignUpClick = {},
            onGoogleAuth = {},
            onForgotPassword = {},
        )
    }
}
