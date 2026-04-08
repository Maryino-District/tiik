package maryino.district.tiik.ui.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tiik.composeapp.generated.resources.*
import maryino.district.tiik.ui.resources.UiText

class EmailVerificationViewModel(
    initialEmail: String,
    private val resendVerificationEmail: suspend (String) -> Unit,
    private val coroutineScopeOverride: CoroutineScope? = null,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailVerificationState(email = initialEmail))
    val uiState: StateFlow<EmailVerificationState> = _uiState.asStateFlow()

    private val _effects = Channel<EmailVerificationEffect>(capacity = Channel.BUFFERED)
    val effects: Flow<EmailVerificationEffect> = _effects.receiveAsFlow()

    private val coroutineScope: CoroutineScope
        get() = coroutineScopeOverride ?: viewModelScope

    fun onIntent(intent: EmailVerificationIntent) {
        when (intent) {
            EmailVerificationIntent.ContinueClicked -> emitEffect(EmailVerificationEffect.Continue)
            EmailVerificationIntent.ResendClicked -> resendVerificationEmail()
        }
    }

    private fun resendVerificationEmail() {
        val email = _uiState.value.email
        if (email.isBlank()) {
            return
        }

        _uiState.update { currentState ->
            currentState.copy(
                isResending = true,
                resendConfirmationMessage = null,
            )
        }

        coroutineScope.launch {
            resendVerificationEmail(email)
            _uiState.update { currentState ->
                currentState.copy(
                    isResending = false,
                    resendConfirmationMessage = UiText.from(Res.string.auth_verification_resent),
                )
            }
        }
    }

    private fun emitEffect(effect: EmailVerificationEffect) {
        val result = _effects.trySend(effect)
        check(result.isSuccess) { "Failed to emit email verification effect: $effect" }
    }
}
