package maryino.district.tiik.ui.navigation

sealed interface TiikDestination {
    val route: String
}

sealed interface TiikTopLevelDestination : TiikDestination {
    val label: String
    val emoji: String
}

data object OnboardingDestination : TiikDestination {
    override val route = "onboarding"
}

data object AuthDestination : TiikDestination {
    override val route = "auth"
}

data object BlocksDestination : TiikTopLevelDestination {
    override val route = "blocks"
    override val label = "Blocks"
    override val emoji = "\uD83D\uDD12"
}

data object PlaceholderDestination : TiikTopLevelDestination {
    override val route = "placeholder"
    override val label = "Soon"
    override val emoji = "\u2728"
}

data object ProfileDestination : TiikTopLevelDestination {
    override val route = "profile"
    override val label = "Profile"
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
    AddBlockDestination.route,
)
