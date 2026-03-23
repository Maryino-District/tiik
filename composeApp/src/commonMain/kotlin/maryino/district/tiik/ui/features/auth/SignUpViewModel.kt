package maryino.district.tiik.ui.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
                            validationMessage = SIGN_UP_ACCOUNT_EXISTS_MESSAGE,
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

    private fun validate(state: SignUpState): String? = when {
        state.email.isBlank() -> SIGN_UP_EMAIL_REQUIRED_MESSAGE
        state.password.isBlank() || state.repeatPassword.isBlank() -> SIGN_UP_PASSWORD_REQUIRED_MESSAGE
        state.password != state.repeatPassword -> SIGN_UP_PASSWORD_MISMATCH_MESSAGE
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

const val SIGN_UP_EMAIL_REQUIRED_MESSAGE = "Enter your email."
const val SIGN_UP_PASSWORD_REQUIRED_MESSAGE = "Enter and confirm your password."
const val SIGN_UP_PASSWORD_MISMATCH_MESSAGE = "Passwords do not match."
const val SIGN_UP_ACCOUNT_EXISTS_MESSAGE = "This email is already registered. Reset your password instead."
