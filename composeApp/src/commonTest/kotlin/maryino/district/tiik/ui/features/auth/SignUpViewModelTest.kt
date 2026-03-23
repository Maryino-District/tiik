package maryino.district.tiik.ui.features.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SignUpViewModelTest {

    private val testScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

    @Test
    fun `submit shows mismatch validation and does not hit backend`() = runBlocking {
        var backendCalls = 0
        val viewModel = SignUpViewModel(
            checkEmailAvailability = {
                backendCalls += 1
                SignUpEmailCheckResult.Available
            },
            coroutineScopeOverride = testScope,
        )

        viewModel.onIntent(SignUpIntent.EmailChanged("user@example.com"))
        viewModel.onIntent(SignUpIntent.PasswordChanged("secret"))
        viewModel.onIntent(SignUpIntent.RepeatPasswordChanged("different"))
        viewModel.onIntent(SignUpIntent.SubmitClicked)

        assertEquals(SIGN_UP_PASSWORD_MISMATCH_MESSAGE, viewModel.uiState.value.validationMessage)
        assertEquals(0, backendCalls)
        assertNull(withTimeoutOrNull(50) { viewModel.effects.firstOrNull() })
    }

    @Test
    fun `available email emits sign up effect`() = runBlocking {
        val viewModel = SignUpViewModel(
            checkEmailAvailability = { SignUpEmailCheckResult.Available },
            coroutineScopeOverride = testScope,
        )

        viewModel.onIntent(SignUpIntent.EmailChanged("user@example.com"))
        viewModel.onIntent(SignUpIntent.PasswordChanged("secret"))
        viewModel.onIntent(SignUpIntent.RepeatPasswordChanged("secret"))
        viewModel.onIntent(SignUpIntent.SubmitClicked)

        val effect = viewModel.effects.firstOrNull()

        assertIs<SignUpEffect.SignUp>(effect)
        assertEquals("user@example.com", effect.email)
        assertEquals("secret", effect.password)
        assertFalse(viewModel.uiState.value.isCheckingEmail)
    }

    @Test
    fun `existing email emits forgot password effect and stores backend message`() = runBlocking {
        val viewModel = SignUpViewModel(
            checkEmailAvailability = { SignUpEmailCheckResult.AlreadyExists },
            coroutineScopeOverride = testScope,
        )

        viewModel.onIntent(SignUpIntent.EmailChanged("user@example.com"))
        viewModel.onIntent(SignUpIntent.PasswordChanged("secret"))
        viewModel.onIntent(SignUpIntent.RepeatPasswordChanged("secret"))
        viewModel.onIntent(SignUpIntent.SubmitClicked)

        val effect = viewModel.effects.firstOrNull()

        assertEquals(SIGN_UP_ACCOUNT_EXISTS_MESSAGE, viewModel.uiState.value.validationMessage)
        assertIs<SignUpEffect.ForgotPassword>(effect)
        assertEquals("user@example.com", effect.email)
    }

    @Test
    fun `submit is enabled only when all fields are filled and not checking`() {
        val emptyState = SignUpState()
        val readyState = SignUpState(
            email = "user@example.com",
            password = "secret",
            repeatPassword = "secret",
        )
        val loadingState = readyState.copy(isCheckingEmail = true)

        assertFalse(emptyState.isSubmitEnabled)
        assertTrue(readyState.isSubmitEnabled)
        assertFalse(loadingState.isSubmitEnabled)
    }
}
