package maryino.district.tiik.ui.features.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private val _effects = Channel<AuthEffect>(capacity = Channel.BUFFERED)
    val effects: Flow<AuthEffect> = _effects.receiveAsFlow()

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.EmailChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(email = intent.value)
                }
            }

            is AuthIntent.PasswordChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(password = intent.value)
                }
            }

            AuthIntent.ToggleModeClicked -> {
                _uiState.update { currentState ->
                    currentState.copy(isLoginMode = !currentState.isLoginMode)
                }
            }

            AuthIntent.SubmitClicked -> emitSubmitEffect()
            AuthIntent.GoogleAuthClicked -> emitEffect(AuthEffect.GoogleAuth)
            AuthIntent.ForgotPasswordClicked -> emitForgotPasswordEffect()
        }
    }

    private fun emitSubmitEffect() {
        val currentState = _uiState.value
        if (!currentState.isSubmitEnabled) {
            return
        }

        val effect = if (currentState.isLoginMode) {
            AuthEffect.SignIn(
                email = currentState.email,
                password = currentState.password,
            )
        } else {
            AuthEffect.SignUp(
                email = currentState.email,
                password = currentState.password,
            )
        }

        emitEffect(effect)
    }

    private fun emitForgotPasswordEffect() {
        if (!_uiState.value.isLoginMode) {
            return
        }

        emitEffect(AuthEffect.ForgotPassword)
    }

    private fun emitEffect(effect: AuthEffect) {
        val result = _effects.trySend(effect)
        check(result.isSuccess) { "Failed to emit auth effect: $effect" }
    }
}
