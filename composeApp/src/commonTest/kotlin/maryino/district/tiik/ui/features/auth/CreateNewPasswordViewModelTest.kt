package maryino.district.tiik.ui.features.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import maryino.district.tiik.ui.resources.UiText
import tiik.composeapp.generated.resources.Res
import tiik.composeapp.generated.resources.auth_sign_up_password_mismatch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull

class CreateNewPasswordViewModelTest {

    private val testScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

    @Test
    fun `submit shows mismatch validation and does not hit backend`() = runBlocking {
        var backendCalls = 0
        val viewModel = CreateNewPasswordViewModel(
            initialEmail = "user@example.com",
            updatePassword = { _, _ -> backendCalls += 1 },
            coroutineScopeOverride = testScope,
        )

        viewModel.onIntent(CreateNewPasswordIntent.PasswordChanged("secret"))
        viewModel.onIntent(CreateNewPasswordIntent.RepeatPasswordChanged("different"))
        viewModel.onIntent(CreateNewPasswordIntent.SubmitClicked)

        assertEquals(
            UiText.from(Res.string.auth_sign_up_password_mismatch),
            viewModel.uiState.value.validationMessage,
        )
        assertEquals(0, backendCalls)
        assertNull(withTimeoutOrNull(50) { viewModel.effects.firstOrNull() })
    }

    @Test
    fun `submit updates password and emits completion effect`() = runBlocking {
        var lastEmail: String? = null
        var lastPassword: String? = null
        val viewModel = CreateNewPasswordViewModel(
            initialEmail = "user@example.com",
            updatePassword = { email, password ->
                lastEmail = email
                lastPassword = password
            },
            coroutineScopeOverride = testScope,
        )

        viewModel.onIntent(CreateNewPasswordIntent.PasswordChanged("secret"))
        viewModel.onIntent(CreateNewPasswordIntent.RepeatPasswordChanged("secret"))
        viewModel.onIntent(CreateNewPasswordIntent.SubmitClicked)

        val effect = viewModel.effects.firstOrNull()

        assertEquals("user@example.com", lastEmail)
        assertEquals("secret", lastPassword)
        assertIs<CreateNewPasswordEffect.PasswordResetCompleted>(effect)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }
}
