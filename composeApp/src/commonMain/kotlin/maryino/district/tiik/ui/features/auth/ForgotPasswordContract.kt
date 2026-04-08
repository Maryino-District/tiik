package maryino.district.tiik.ui.features.auth

import maryino.district.tiik.ui.resources.UiText

data class ForgotPasswordState(
    val email: String = "",
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val validationMessage: UiText? = null,
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
