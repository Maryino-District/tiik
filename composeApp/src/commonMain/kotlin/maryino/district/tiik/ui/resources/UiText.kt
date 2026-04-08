package maryino.district.tiik.ui.resources

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed interface UiText {
    data class Resource(
        val resource: StringResource,
        val args: List<Any> = emptyList(),
    ) : UiText

    data class Dynamic(val value: String) : UiText

    companion object {
        fun from(resource: StringResource, vararg args: Any): UiText = Resource(
            resource = resource,
            args = args.toList(),
        )
    }
}

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Dynamic -> value
    is UiText.Resource -> stringResource(resource, *args.toTypedArray())
}
