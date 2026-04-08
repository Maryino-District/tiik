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

class SignUpViewModel(
    private val checkEmailAvailability: suspend (String) -> SignUpEmailCheckResult,
    private val coroutineScopeOverride: CoroutineScope? = null,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpState())
    val uiState: StateFlow<SignUpState> = _uiState.asStateFlow()

    private val _effects = Channel<SignUpEffect>(capacity = Channel.BUFFERED)
    val effects: Flow<SignUpEffect> = _effects.receiveAsFlow()

    private val coroutineScope: CoroutineScope
        get() = coroutineScopeOverride ?: viewModelScope

    fun onIntent(intent: SignUpIntent) {
        when (intent) {
            is SignUpIntent.EmailChanged -> updateState {
                copy(
                    email = intent.value,
                    validationMessage = null,
                )
            }

            is SignUpIntent.PasswordChanged -> updateState {
                copy(
                    password = intent.value,
                    validationMessage = null,
                )
            }

            is SignUpIntent.RepeatPasswordChanged -> updateState {
                copy(
                    repeatPassword = intent.value,
                    validationMessage = null,
                )
            }

            SignUpIntent.SubmitClicked -> submit()
            SignUpIntent.ForgotPasswordClicked -> emitForgotPasswordIfNeeded()
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
                isCheckingEmail = true,
                validationMessage = null,
            )
        }

        coroutineScope.launch {
            when (checkEmailAvailability(state.email)) {
                SignUpEmailCheckResult.Available -> {
                    updateState {
                        copy(isCheckingEmail = false)
                    }
                    emitEffect(
                        SignUpEffect.SignUp(
                            email = state.email,
                            password = state.password,
                        )
                    )
                }

                SignUpEmailCheckResult.AlreadyExists -> {
                    updateState {
                        copy(
                            isCheckingEmail = false,
                            validationMessage = UiText.from(Res.string.auth_sign_up_account_exists),
                        )
                    }
                    emitEffect(SignUpEffect.ForgotPassword(state.email))
                }
            }
        }
    }

    private fun emitForgotPasswordIfNeeded() {
        val email = _uiState.value.email
        if (email.isBlank()) {
            return
        }

        emitEffect(SignUpEffect.ForgotPassword(email))
    }

    private fun validate(state: SignUpState): UiText? = when {
        state.email.isBlank() -> UiText.from(Res.string.auth_sign_up_email_required)
        state.password.isBlank() || state.repeatPassword.isBlank() -> UiText.from(
            Res.string.auth_sign_up_password_required,
        )
        state.password != state.repeatPassword -> UiText.from(Res.string.auth_sign_up_password_mismatch)
        else -> null
    }

    private fun updateState(transform: SignUpState.() -> SignUpState) {
        _uiState.update(transform)
    }

    private fun emitEffect(effect: SignUpEffect) {
        val result = _effects.trySend(effect)
        check(result.isSuccess) { "Failed to emit sign up effect: $effect" }
    }
}
