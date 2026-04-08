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
import maryino.district.tiik.ui.resources.UiText
import tiik.composeapp.generated.resources.Res
import tiik.composeapp.generated.resources.auth_sign_up_password_mismatch
import tiik.composeapp.generated.resources.auth_sign_up_password_required

class CreateNewPasswordViewModel(
    initialEmail: String,
    private val updatePassword: suspend (String, String) -> Unit,
    private val coroutineScopeOverride: CoroutineScope? = null,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateNewPasswordState(email = initialEmail))
    val uiState: StateFlow<CreateNewPasswordState> = _uiState.asStateFlow()

    private val _effects = Channel<CreateNewPasswordEffect>(capacity = Channel.BUFFERED)
    val effects: Flow<CreateNewPasswordEffect> = _effects.receiveAsFlow()

    private val coroutineScope: CoroutineScope
        get() = coroutineScopeOverride ?: viewModelScope

    fun onIntent(intent: CreateNewPasswordIntent) {
        when (intent) {
            is CreateNewPasswordIntent.PasswordChanged -> updateState {
                copy(
                    password = intent.value,
                    validationMessage = null,
                )
            }

            is CreateNewPasswordIntent.RepeatPasswordChanged -> updateState {
                copy(
                    repeatPassword = intent.value,
                    validationMessage = null,
                )
            }

            CreateNewPasswordIntent.SubmitClicked -> submit()
        }
    }

    private fun submit() {
        val state = _uiState.value
        val validationMessage = validate(state)
        if (validationMessage != null) {
            updateState {
                copy(validationMessage = validationMessage)
            }
            return
        }

        updateState {
            copy(
                isSubmitting = true,
                validationMessage = null,
            )
        }

        coroutineScope.launch {
            updatePassword(state.email, state.password)
            updateState {
                copy(isSubmitting = false)
            }
            emitEffect(CreateNewPasswordEffect.PasswordResetCompleted)
        }
    }

    private fun validate(state: CreateNewPasswordState): UiText? = when {
        state.password.isBlank() || state.repeatPassword.isBlank() -> {
            UiText.from(Res.string.auth_sign_up_password_required)
        }

        state.password != state.repeatPassword -> {
            UiText.from(Res.string.auth_sign_up_password_mismatch)
        }

        else -> null
    }

    private fun updateState(transform: CreateNewPasswordState.() -> CreateNewPasswordState) {
        _uiState.update(transform)
    }

    private fun emitEffect(effect: CreateNewPasswordEffect) {
        val result = _effects.trySend(effect)
        check(result.isSuccess) { "Failed to emit create new password effect: $effect" }
    }
}
