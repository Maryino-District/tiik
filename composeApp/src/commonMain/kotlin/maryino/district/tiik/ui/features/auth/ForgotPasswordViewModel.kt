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

class ForgotPasswordViewModel(
    initialEmail: String,
    private val requestPasswordReset: suspend (String) -> Unit,
    private val coroutineScopeOverride: CoroutineScope? = null,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordState(email = initialEmail))
    val uiState: StateFlow<ForgotPasswordState> = _uiState.asStateFlow()

    private val _effects = Channel<ForgotPasswordEffect>(capacity = Channel.BUFFERED)
    val effects: Flow<ForgotPasswordEffect> = _effects.receiveAsFlow()

    private val coroutineScope: CoroutineScope
        get() = coroutineScopeOverride ?: viewModelScope

    fun onIntent(intent: ForgotPasswordIntent) {
        when (intent) {
            is ForgotPasswordIntent.EmailChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        email = intent.value,
                        validationMessage = null,
                    )
                }
            }

            ForgotPasswordIntent.SubmitClicked -> submit()
            ForgotPasswordIntent.BackToSignInClicked -> emitEffect(ForgotPasswordEffect.BackToSignIn)
        }
    }

    private fun submit() {
        val email = _uiState.value.email
        if (email.isBlank()) {
            _uiState.update { currentState ->
                currentState.copy(
                    validationMessage = UiText.from(Res.string.auth_forgot_password_email_required),
                )
            }
            return
        }

        _uiState.update { currentState ->
            currentState.copy(
                isSubmitting = true,
                validationMessage = null,
            )
        }

        coroutineScope.launch {
            requestPasswordReset(email)
            _uiState.update { currentState ->
                currentState.copy(
                    isSubmitting = false,
                    isSuccess = true,
                )
            }
        }
    }

    private fun emitEffect(effect: ForgotPasswordEffect) {
        val result = _effects.trySend(effect)
        check(result.isSuccess) { "Failed to emit forgot password effect: $effect" }
    }
}
