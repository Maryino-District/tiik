package maryino.district.tiik.ui.features.auth

data class AuthState(
    val isLoginMode: Boolean = true,
    val email: String = "",
    val password: String = "",
) {
    val isSubmitEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank()
}

sealed interface AuthIntent {
    data class EmailChanged(val value: String) : AuthIntent
    data class PasswordChanged(val value: String) : AuthIntent
    data object ToggleModeClicked : AuthIntent
    data object SubmitClicked : AuthIntent
    data object GoogleAuthClicked : AuthIntent
    data object ForgotPasswordClicked : AuthIntent
}

sealed interface AuthEffect {
    data class SignIn(val email: String, val password: String) : AuthEffect
    data class SignUp(val email: String, val password: String) : AuthEffect
    data object GoogleAuth : AuthEffect
    data object ForgotPassword : AuthEffect
}
