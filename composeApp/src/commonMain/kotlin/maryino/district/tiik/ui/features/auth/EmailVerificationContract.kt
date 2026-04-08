package maryino.district.tiik.ui.features.auth

import maryino.district.tiik.ui.resources.UiText

data class EmailVerificationState(
    val email: String = "",
    val isResending: Boolean = false,
    val resendConfirmationMessage: UiText? = null,
) {
    val isContinueEnabled: Boolean
        get() = !isResending

    val isResendEnabled: Boolean
        get() = email.isNotBlank() && !isResending
}

sealed interface EmailVerificationIntent {
    data object ContinueClicked : EmailVerificationIntent
    data object ResendClicked : EmailVerificationIntent
}

sealed interface EmailVerificationEffect {
    data object Continue : EmailVerificationEffect
}
