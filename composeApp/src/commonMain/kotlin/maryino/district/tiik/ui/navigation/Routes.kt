package maryino.district.tiik.ui.navigation

import tiik.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

sealed interface TiikDestination {
    val route: String
}

sealed interface TiikTopLevelDestination : TiikDestination {
    val label: StringResource
    val emoji: String
}

data object OnboardingDestination : TiikDestination {
    override val route = "onboarding"
}

data object AuthDestination : TiikDestination {
    override val route = "auth"
}

data object SignUpDestination : TiikDestination {
    override val route = "sign_up"
}

data object EmailVerificationDestination : TiikDestination {
    override val route = "email_verification"
}

data object ForgotPasswordDestination : TiikDestination {
    override val route = "forgot_password"
}

data object BlocksDestination : TiikTopLevelDestination {
    override val route = "blocks"
    override val label = Res.string.nav_blocks
    override val emoji = "\uD83D\uDD12"
}

data object PlaceholderDestination : TiikTopLevelDestination {
    override val route = "placeholder"
    override val label = Res.string.nav_soon
    override val emoji = "\u2728"
}

data object ProfileDestination : TiikTopLevelDestination {
    override val route = "profile"
    override val label = Res.string.nav_profile
    override val emoji = "\uD83D\uDC64"
}

data object AddBlockDestination : TiikDestination {
    override val route = "add_block"
}

data object UnlockDestination : TiikDestination {
    override val route = "unlock/{blockId}"

    fun createRoute(blockId: String): String = "unlock/$blockId"
}

val tiikTopLevelDestinations = listOf(
    BlocksDestination,
    PlaceholderDestination,
    ProfileDestination,
)

val routesWithoutNav = setOf(
    OnboardingDestination.route,
    AuthDestination.route,
    SignUpDestination.route,
    EmailVerificationDestination.route,
    ForgotPasswordDestination.route,
    AddBlockDestination.route,
)
