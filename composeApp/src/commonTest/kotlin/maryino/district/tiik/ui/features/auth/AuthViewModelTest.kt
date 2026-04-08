package maryino.district.tiik.ui.features.auth

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AuthViewModelTest {

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
    fun `submit emits sign in effect when form is valid`() = runBlocking {
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
    fun `google auth emits effect`() = runBlocking {
        val viewModel = AuthViewModel()

        viewModel.onIntent(AuthIntent.GoogleAuthClicked)

        assertEquals(AuthEffect.GoogleAuth, viewModel.effects.firstOrNull())
    }

    @Test
    fun `forgot password emits email effect`() = runBlocking {
        val viewModel = AuthViewModel()

        viewModel.onIntent(AuthIntent.EmailChanged("user@example.com"))
        viewModel.onIntent(AuthIntent.ForgotPasswordClicked)

        val effect = viewModel.effects.firstOrNull()

        assertIs<AuthEffect.ForgotPassword>(effect)
        assertEquals("user@example.com", effect.email)
    }
}
