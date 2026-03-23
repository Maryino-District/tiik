package maryino.district.tiik.ui.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import maryino.district.tiik.ui.components.*
import maryino.district.tiik.ui.theme.*

// ─────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────

@Composable
fun AuthScreen(
    onSignIn: (email: String, password: String) -> Unit,
    onSignUp: (email: String, password: String) -> Unit,
    onGoogleAuth: () -> Unit,
    onForgotPassword: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    authViewModel: AuthViewModel = viewModel { AuthViewModel() },
) {
    val state by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(authViewModel, onSignIn, onSignUp, onGoogleAuth, onForgotPassword) {
        authViewModel.effects.collectLatest { effect ->
            when (effect) {
                is AuthEffect.SignIn -> onSignIn(effect.email, effect.password)
                is AuthEffect.SignUp -> onSignUp(effect.email, effect.password)
                AuthEffect.GoogleAuth -> onGoogleAuth()
                AuthEffect.ForgotPassword -> onForgotPassword()
            }
        }
    }

    AuthScreenContent(
        state = state,
        onIntent = authViewModel::onIntent,
        modifier = modifier,
        isLoading = isLoading,
    )
}

@Composable
private fun AuthScreenContent(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
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
            text = if (state.isLoginMode) "Welcome back" else "Create account",
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(Spacing.xl))

        // ── Form ─────────────────────────────────────────────
        TiikTextField(
            value = state.email,
            onValueChange = { onIntent(AuthIntent.EmailChanged(it)) },
            placeholder = "you@example.com",
            label = "Email",
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.sm))

        TiikTextField(
            value = state.password,
            onValueChange = { onIntent(AuthIntent.PasswordChanged(it)) },
            placeholder = "••••••••",
            label = "Password",
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.isLoginMode) {
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "Forgot password?",
                style = MaterialTheme.typography.bodySmall,
                color = TiikColors.Ink3,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onIntent(AuthIntent.ForgotPasswordClicked) },
            )
        }

        Spacer(Modifier.height(Spacing.xl))

        // ── Primary CTA ───────────────────────────────────────
        TiikButton(
            text = if (state.isLoginMode) "Sign in" else "Create account",
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
            Text("or", style = MaterialTheme.typography.bodySmall, color = TiikColors.Ink3)
            TiikDivider(Modifier.weight(1f))
        }

        Spacer(Modifier.height(Spacing.lg))

        // ── Google ────────────────────────────────────────────
        TiikButton(
            text = "Continue with Google",
            onClick = { onIntent(AuthIntent.GoogleAuthClicked) },
            style = TiikButtonStyle.Light,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.weight(1f))

        // ── Toggle login / register ───────────────────────────
        val toggleText = buildAnnotatedString {
            append(if (state.isLoginMode) "No account? " else "Already have one? ")
            withStyle(SpanStyle(color = TiikColors.Ink, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)) {
                append(if (state.isLoginMode) "Sign up" else "Sign in")
            }
        }
        Text(
            text = toggleText,
            style = MaterialTheme.typography.bodySmall,
            color = TiikColors.Ink3,
            modifier = Modifier
                .padding(bottom = Spacing.x3l)
                .clickable { onIntent(AuthIntent.ToggleModeClicked) },
        )
    }
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
                text = "ii",
                style = MaterialTheme.typography.headlineMedium,
                color = TiikColors.InkOnDark,
            )
        }
        // Wordmark
        Text(
            text = "tiik",
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
            onSignUp = { _, _ -> },
            onGoogleAuth = {},
            onForgotPassword = {},
        )
    }
}
