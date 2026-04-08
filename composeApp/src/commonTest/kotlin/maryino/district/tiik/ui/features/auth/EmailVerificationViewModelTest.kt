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

class EmailVerificationViewModelTest {

    private val testScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

    @Test
    fun `continue emits continue effect`() {
        runBlocking {
            val viewModel = EmailVerificationViewModel(
                initialEmail = "user@example.com",
                resendVerificationEmail = {},
                coroutineScopeOverride = testScope,
            )

            viewModel.onIntent(EmailVerificationIntent.ContinueClicked)

            val effect = viewModel.effects.firstOrNull()

            assertIs<EmailVerificationEffect.Continue>(effect)
        }
    }

    @Test
    fun `resend calls backend and stores confirmation message`() = runBlocking {
        var backendCalls = 0
        val viewModel = EmailVerificationViewModel(
            initialEmail = "user@example.com",
            resendVerificationEmail = {
                backendCalls += 1
            },
            coroutineScopeOverride = testScope,
        )

        viewModel.onIntent(EmailVerificationIntent.ResendClicked)

        assertEquals(1, backendCalls)
        assertEquals(
            UiText.from(Res.string.auth_verification_resent),
            viewModel.uiState.value.resendConfirmationMessage,
        )
        assertFalse(viewModel.uiState.value.isResending)
    }

    @Test
    fun `resend stays disabled when email is blank`() {
        val emptyState = EmailVerificationState()
        val loadingState = EmailVerificationState(
            email = "user@example.com",
            isResending = true,
        )
        val readyState = EmailVerificationState(email = "user@example.com")

        assertFalse(emptyState.isResendEnabled)
        assertFalse(loadingState.isResendEnabled)
        assertTrue(readyState.isResendEnabled)
        assertFalse(loadingState.isContinueEnabled)
        assertTrue(readyState.isContinueEnabled)
    }
}
