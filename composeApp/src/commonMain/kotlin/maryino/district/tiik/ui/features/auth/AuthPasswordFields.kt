package maryino.district.tiik.ui.features.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import maryino.district.tiik.ui.components.TiikTextField
import maryino.district.tiik.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import tiik.composeapp.generated.resources.Res
import tiik.composeapp.generated.resources.auth_repeat_password
import tiik.composeapp.generated.resources.common_password_label
import tiik.composeapp.generated.resources.common_password_placeholder

@Composable
internal fun AuthPasswordFields(
    password: String,
    repeatPassword: String,
    onPasswordChanged: (String) -> Unit,
    onRepeatPasswordChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        TiikTextField(
            value = password,
            onValueChange = onPasswordChanged,
            placeholder = stringResource(Res.string.common_password_placeholder),
            label = stringResource(Res.string.common_password_label),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.sm))

        TiikTextField(
            value = repeatPassword,
            onValueChange = onRepeatPasswordChanged,
            placeholder = stringResource(Res.string.common_password_placeholder),
            label = stringResource(Res.string.auth_repeat_password),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
