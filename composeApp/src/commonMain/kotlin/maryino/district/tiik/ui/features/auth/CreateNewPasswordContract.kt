package maryino.district.tiik.ui.features.auth

import maryino.district.tiik.ui.resources.UiText

data class CreateNewPasswordState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isSubmitting: Boolean = false,
    val validationMessage: UiText? = null,
) {
    val isSubmitEnabled: Boolean
        get() = password.isNotBlank() &&
            repeatPassword.isNotBlank() &&
            !isSubmitting
}

sealed interface CreateNewPasswordIntent {
    data class PasswordChanged(val value: String) : CreateNewPasswordIntent
    data class RepeatPasswordChanged(val value: String) : CreateNewPasswordIntent
    data object SubmitClicked : CreateNewPasswordIntent
}

sealed interface CreateNewPasswordEffect {
    data object PasswordResetCompleted : CreateNewPasswordEffect
}
