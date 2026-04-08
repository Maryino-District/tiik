package maryino.district.tiik.ui.features.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import maryino.district.tiik.ui.resources.UiText
import tiik.composeapp.generated.resources.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ForgotPasswordViewModelTest {

    private val testScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

    @Test
    fun `blank email stores validation message`() {
        val viewModel = ForgotPasswordViewModel(
            initialEmail = "",
            requestPasswordReset = {},
            checkResetPasswordStatus = { ForgotPasswordResetStatusResult.AwaitingConfirmation },
            coroutineScopeOverride = testScope,
        )

        viewModel.onIntent(ForgotPasswordIntent.SubmitClicked)

        assertEquals(
            UiText.from(Res.string.auth_forgot_password_email_required),
            viewModel.uiState.value.validationMessage,
        )
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `submit marks request as successful`() = runBlocking {
        var requestedEmail: String? = null
        val viewModel = ForgotPasswordViewModel(
            initialEmail = "user@example.com",
            requestPasswordReset = { requestedEmail = it },
            checkResetPasswordStatus = { ForgotPasswordResetStatusResult.AwaitingConfirmation },
            coroutineScopeOverride = testScope,
        )

        viewModel.onIntent(ForgotPasswordIntent.SubmitClicked)

        assertEquals("user@example.com", requestedEmail)
        assertTrue(viewModel.uiState.value.isResetEmailSent)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `continue shows waiting message when email link is not confirmed`() = runBlocking {
        val viewModel = ForgotPasswordViewModel(
            initialEmail = "user@example.com",
            requestPasswordReset = {},
            checkResetPasswordStatus = { ForgotPasswordResetStatusResult.AwaitingConfirmation },
            coroutineScopeOverride = testScope,
        )

        viewModel.onIntent(ForgotPasswordIntent.SubmitClicked)
        viewModel.onIntent(ForgotPasswordIntent.ContinueClicked)

        val effect = viewModel.effects.firstOrNull()

        assertIs<ForgotPasswordEffect.ShowMessage>(effect)
        assertEquals(
            UiText.from(Res.string.auth_forgot_password_wait_for_email_confirmation),
            effect.message,
        )
        assertFalse(viewModel.uiState.value.isCheckingResetStatus)
    }

    @Test
    fun `continue navigates to password creation after confirmation`() = runBlocking {
        val viewModel = ForgotPasswordViewModel(
            initialEmail = "confirmed@tiik.app",
            requestPasswordReset = {},
            checkResetPasswordStatus = { ForgotPasswordResetStatusResult.Confirmed },
            coroutineScopeOverride = testScope,
        )

        viewModel.onIntent(ForgotPasswordIntent.SubmitClicked)
        viewModel.onIntent(ForgotPasswordIntent.ContinueClicked)

        val effect = viewModel.effects.firstOrNull()

        assertIs<ForgotPasswordEffect.NavigateToCreateNewPassword>(effect)
        assertEquals("confirmed@tiik.app", effect.email)
        assertFalse(viewModel.uiState.value.isCheckingResetStatus)
    }
}
