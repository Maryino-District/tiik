package maryino.district.tiik.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import maryino.district.tiik.ui.features.addblock.AddBlockScreen
import maryino.district.tiik.ui.features.addblock.FriendUser
import maryino.district.tiik.ui.features.addblock.InstalledApp
import maryino.district.tiik.ui.features.auth.AuthScreen
import maryino.district.tiik.ui.features.blocks.BlockItem
import maryino.district.tiik.ui.features.blocks.BlockStatus
import maryino.district.tiik.ui.features.blocks.BlocksScreen
import maryino.district.tiik.ui.features.blocks.GuardianRequest
import maryino.district.tiik.ui.features.onboarding.OnboardingScreen
import maryino.district.tiik.ui.features.onboarding.samplePermissions
import maryino.district.tiik.ui.features.placeholder.PlaceholderScreen
import maryino.district.tiik.ui.features.profile.ProfileScreen
import maryino.district.tiik.ui.features.profile.UserProfile
import maryino.district.tiik.ui.features.unlock.UnlockRequestScreen
import maryino.district.tiik.ui.features.unlock.UnlockScreenState
import maryino.district.tiik.ui.theme.TiikTheme

@Composable
fun TiikApp(
    isOnboardingComplete: Boolean,
    isLoggedIn: Boolean,
) {
    TiikTheme {
        val navController = rememberNavController()
        val backStack by navController.currentBackStackEntryAsState()
        val currentRoute = backStack?.destination?.route

        val startDestination = when {
            !isOnboardingComplete -> OnboardingDestination.route
            !isLoggedIn -> AuthDestination.route
            else -> BlocksDestination.route
        }

        Scaffold(
            bottomBar = {
                if (currentRoute !in routesWithoutNav) {
                    TiikBottomNav(
                        currentRoute = currentRoute ?: BlocksDestination.route,
                        onNavigate = { destination ->
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(OnboardingDestination.route) {
                    OnboardingScreen(
                        permissions = samplePermissions(),
                        currentStep = 2,
                        onRequestPermission = {
                            navController.navigate(AuthDestination.route) {
                                popUpTo(OnboardingDestination.route) { inclusive = true }
                            }
                        },
                    )
                }

                composable(AuthDestination.route) {
                    AuthScreen(
                        onSignIn = { _, _ ->
                            navController.navigate(BlocksDestination.route) {
                                popUpTo(AuthDestination.route) { inclusive = true }
                            }
                        },
                        onSignUp = { _, _ ->
                            navController.navigate(BlocksDestination.route) {
                                popUpTo(AuthDestination.route) { inclusive = true }
                            }
                        },
                        onGoogleAuth = {},
                        onForgotPassword = {},
                    )
                }

                composable(BlocksDestination.route) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        BlocksScreen(
                            myBlocks = sampleBlocks(),
                            guardianRequests = sampleGuardianRequests(),
                            onAddBlock = { navController.navigate(AddBlockDestination.route) },
                            onBlockClick = { block ->
                                navController.navigate(UnlockDestination.createRoute(block.id))
                            },
                            onApproveRequest = {},
                            onDenyRequest = {},
                        )
                    }
                }

                composable(AddBlockDestination.route) {
                    AddBlockScreen(
                        installedApps = sampleApps(),
                        friends = sampleFriends(),
                        onDismiss = { navController.popBackStack() },
                        onBlockCreated = { _, _ -> navController.popBackStack() },
                    )
                }

                composable(UnlockDestination.route) {
                    var state by remember { mutableStateOf<UnlockScreenState>(UnlockScreenState.Form) }

                    UnlockRequestScreen(
                        appName = "Instagram",
                        guardianUsername = "maksim_k",
                        guardianDisplayName = "Maksim K.",
                        screenState = state,
                        onSendRequest = { state = UnlockScreenState.Waiting },
                        onBack = { navController.popBackStack() },
                        onOpenApp = { navController.popBackStack() },
                    )
                }

                composable(PlaceholderDestination.route) {
                    PlaceholderScreen(
                        title = "Future feature slot",
                        description = "This destination is intentionally reserved for the next top-level feature.",
                    )
                }

                composable(ProfileDestination.route) {
                    ProfileScreen(
                        profile = sampleProfile(),
                        onEditUsername = {},
                        onEditEmail = {},
                        onToggleNotifications = {},
                        onUpgradePro = {},
                        onSignOut = {},
                    )
                }
            }
        }
    }
}

private fun sampleBlocks() = listOf(
    BlockItem("1", "Instagram", "com.instagram.android", "maksim_k", BlockStatus.Locked),
    BlockItem("2", "TikTok", "com.zhiliaoapp.musically", "anna_dev", BlockStatus.Pending),
    BlockItem("3", "YouTube", "com.google.android.youtube", "maksim_k", BlockStatus.Unlocked),
)

private fun sampleGuardianRequests() = listOf(
    GuardianRequest("r1", "dima_work", "TikTok", "Need it for research"),
)

private fun sampleApps() = listOf(
    InstalledApp("com.instagram.android", "Instagram"),
    InstalledApp("com.zhiliaoapp.musically", "TikTok"),
    InstalledApp("com.google.android.youtube", "YouTube"),
    InstalledApp("com.twitter.android", "Twitter / X"),
    InstalledApp("com.reddit.frontpage", "Reddit"),
)

private fun sampleFriends() = listOf(
    FriendUser("f1", "maksim_k", "Maksim K."),
    FriendUser("f2", "anna_dev", "Anna Dev"),
    FriendUser("f3", "dima_work", "Dima Work"),
)

private fun sampleProfile() = UserProfile(
    displayName = "Alex Petrov",
    username = "alex_focus",
    email = "alex@gmail.com",
    activeBlocksCount = 2,
    friendsCount = 3,
    heldStrongCount = 47,
    isPro = false,
    notificationsEnabled = true,
)
