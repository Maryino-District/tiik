package maryino.district.tiik.ui.features.auth

import maryino.district.tiik.ui.resources.UiText

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isCheckingEmail: Boolean = false,
    val validationMessage: UiText? = null,
) {
    val isSubmitEnabled: Boolean
        get() = email.isNotBlank() &&
            password.isNotBlank() &&
            repeatPassword.isNotBlank() &&
            !isCheckingEmail
}

sealed interface SignUpIntent {
    data class EmailChanged(val value: String) : SignUpIntent
    data class PasswordChanged(val value: String) : SignUpIntent
    data class RepeatPasswordChanged(val value: String) : SignUpIntent
    data object SubmitClicked : SignUpIntent
    data object ForgotPasswordClicked : SignUpIntent
}

sealed interface SignUpEffect {
    data class SignUp(val email: String, val password: String) : SignUpEffect
    data class ForgotPassword(val email: String) : SignUpEffect
}

sealed interface SignUpEmailCheckResult {
    data object Available : SignUpEmailCheckResult
    data object AlreadyExists : SignUpEmailCheckResult
}
