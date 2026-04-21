package maryino.district.tiik.ui.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.util.logging.Logger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import maryino.district.tiik.api.AuthApiImpl
import maryino.district.tiik.models.RegisterRequest

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
        val authImpl = AuthApiImpl("http://10.0.2.2:8080",)
        viewModelScope.launch {
            // Используем 10.0.2.2 для эмулятора или реальный IP для устройства
            val response = authImpl.register(
                RegisterRequest(
                    email = currentState.email,
                    password = currentState.password,
                )
            )
            println("crab response = ${response}")
        }
        emitEffect(
            AuthEffect.SignIn(
                email = currentState.email,
                password = currentState.password,
            )
        )
    }

    private fun emitEffect(effect: AuthEffect) {
        val result = _effects.trySend(effect)
        check(result.isSuccess) { "Failed to emit auth effect: $effect" }
    }

    private fun emitForgotPasswordEffect() {
        emitEffect(AuthEffect.ForgotPassword(_uiState.value.email))
    }
}
