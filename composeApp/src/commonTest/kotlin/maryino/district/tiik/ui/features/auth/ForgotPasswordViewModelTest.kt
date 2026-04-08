package maryino.district.tiik.ui.features.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import maryino.district.tiik.ui.resources.UiText
import tiik.composeapp.generated.resources.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ForgotPasswordViewModelTest {

    private val testScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

    @Test
    fun `blank email stores validation message`() {
        val viewModel = ForgotPasswordViewModel(
            initialEmail = "",
            requestPasswordReset = {},
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
            coroutineScopeOverride = testScope,
        )

        viewModel.onIntent(ForgotPasswordIntent.SubmitClicked)

        assertEquals("user@example.com", requestedEmail)
        assertTrue(viewModel.uiState.value.isSuccess)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }
}
