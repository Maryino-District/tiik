package maryino.district.tiik.ui.features.auth

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthViewModelTest {

    @Test
    fun `toggle mode switches between login and sign up`() {
        val viewModel = AuthViewModel()

        assertTrue(viewModel.uiState.value.isLoginMode)

        viewModel.onIntent(AuthIntent.ToggleModeClicked)

        assertFalse(viewModel.uiState.value.isLoginMode)
    }

    @Test
    fun `changing credentials updates ui state`() {
        val viewModel = AuthViewModel()

        viewModel.onIntent(AuthIntent.EmailChanged("user@example.com"))
        viewModel.onIntent(AuthIntent.PasswordChanged("secret"))

        assertEquals("user@example.com", viewModel.uiState.value.email)
        assertEquals("secret", viewModel.uiState.value.password)
    }

    @Test
    fun `submit is enabled only when email and password are not blank`() {
        val viewModel = AuthViewModel()

        assertFalse(viewModel.uiState.value.isSubmitEnabled)

        viewModel.onIntent(AuthIntent.EmailChanged("user@example.com"))
        assertFalse(viewModel.uiState.value.isSubmitEnabled)

        viewModel.onIntent(AuthIntent.PasswordChanged("secret"))
        assertTrue(viewModel.uiState.value.isSubmitEnabled)
    }

    @Test
    fun `submit emits sign in effect in login mode`() = runBlocking {
        val viewModel = AuthViewModel()

        viewModel.onIntent(AuthIntent.EmailChanged("user@example.com"))
        viewModel.onIntent(AuthIntent.PasswordChanged("secret"))
        viewModel.onIntent(AuthIntent.SubmitClicked)

        val effect = viewModel.effects.firstOrNull()

        assertIs<AuthEffect.SignIn>(effect)
        assertEquals("user@example.com", effect.email)
        assertEquals("secret", effect.password)
    }

    @Test
    fun `submit emits sign up effect in sign up mode`() = runBlocking {
        val viewModel = AuthViewModel()

        viewModel.onIntent(AuthIntent.ToggleModeClicked)
        viewModel.onIntent(AuthIntent.EmailChanged("user@example.com"))
        viewModel.onIntent(AuthIntent.PasswordChanged("secret"))
        viewModel.onIntent(AuthIntent.SubmitClicked)

        val effect = viewModel.effects.firstOrNull()

        assertIs<AuthEffect.SignUp>(effect)
        assertEquals("user@example.com", effect.email)
        assertEquals("secret", effect.password)
    }

    @Test
    fun `forgot password emits effect only in login mode`() = runBlocking {
        val loginViewModel = AuthViewModel()

        loginViewModel.onIntent(AuthIntent.ForgotPasswordClicked)
        val loginEffect = loginViewModel.effects.firstOrNull()

        assertEquals(AuthEffect.ForgotPassword, loginEffect)

        val signUpViewModel = AuthViewModel()
        signUpViewModel.onIntent(AuthIntent.ToggleModeClicked)
        signUpViewModel.onIntent(AuthIntent.ForgotPasswordClicked)

        val signUpEffect = withTimeoutOrNull(50) { signUpViewModel.effects.firstOrNull() }

        assertNull(signUpEffect)
    }
}
