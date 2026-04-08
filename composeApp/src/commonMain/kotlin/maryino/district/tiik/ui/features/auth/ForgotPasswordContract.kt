package maryino.district.tiik.ui.features.auth

import maryino.district.tiik.ui.resources.UiText

data class ForgotPasswordState(
    val email: String = "",
    val isSubmitting: Boolean = false,
    val isCheckingResetStatus: Boolean = false,
    val isResetEmailSent: Boolean = false,
    val validationMessage: UiText? = null,
) {
    val isSubmitEnabled: Boolean
        get() = email.isNotBlank() && !isSubmitting && !isResetEmailSent

    val isContinueEnabled: Boolean
        get() = email.isNotBlank() && isResetEmailSent && !isCheckingResetStatus
}

sealed interface ForgotPasswordIntent {
    data class EmailChanged(val value: String) : ForgotPasswordIntent
    data object SubmitClicked : ForgotPasswordIntent
    data object ContinueClicked : ForgotPasswordIntent
    data object BackToSignInClicked : ForgotPasswordIntent
}

sealed interface ForgotPasswordEffect {
    data object BackToSignIn : ForgotPasswordEffect
    data class ShowMessage(val message: UiText) : ForgotPasswordEffect
    data class NavigateToCreateNewPassword(val email: String) : ForgotPasswordEffect
}

sealed interface ForgotPasswordResetStatusResult {
    data object AwaitingConfirmation : ForgotPasswordResetStatusResult
    data object Confirmed : ForgotPasswordResetStatusResult
}
