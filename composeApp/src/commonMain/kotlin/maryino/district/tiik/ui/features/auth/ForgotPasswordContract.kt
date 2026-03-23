package maryino.district.tiik.ui.features.auth

data class ForgotPasswordState(
    val email: String = "",
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val validationMessage: String? = null,
) {
    val isSubmitEnabled: Boolean
        get() = email.isNotBlank() && !isSubmitting && !isSuccess
}

sealed interface ForgotPasswordIntent {
    data class EmailChanged(val value: String) : ForgotPasswordIntent
    data object SubmitClicked : ForgotPasswordIntent
    data object BackToSignInClicked : ForgotPasswordIntent
}

sealed interface ForgotPasswordEffect {
    data object BackToSignIn : ForgotPasswordEffect
}

const val FORGOT_PASSWORD_EMAIL_REQUIRED_MESSAGE = "Enter your email."
const val FORGOT_PASSWORD_SUCCESS_MESSAGE =
    "If an account with this email exists, we sent a reset link."
