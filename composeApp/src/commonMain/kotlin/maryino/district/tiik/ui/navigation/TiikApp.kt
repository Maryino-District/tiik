package maryino.district.tiik.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import tiik.composeapp.generated.resources.*
import maryino.district.tiik.ui.features.addblock.AddBlockScreen
import maryino.district.tiik.ui.features.addblock.FriendUser
import maryino.district.tiik.ui.features.addblock.InstalledApp
import maryino.district.tiik.ui.features.auth.AuthScreen
import maryino.district.tiik.ui.features.auth.CreateNewPasswordScreen
import maryino.district.tiik.ui.features.auth.EmailVerificationScreen
import maryino.district.tiik.ui.features.auth.ForgotPasswordScreen
import maryino.district.tiik.ui.features.auth.ForgotPasswordResetStatusResult
import maryino.district.tiik.ui.features.auth.SignUpEmailCheckResult
import maryino.district.tiik.ui.features.auth.SignUpScreen
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
import org.jetbrains.compose.resources.stringResource

private const val AuthTransitionDurationMs = 320

@Composable
fun TiikApp(
    isOnboardingComplete: Boolean,
    isLoggedIn: Boolean,
) {
    TiikTheme {
        val navController = rememberNavController()
        val backStack by navController.currentBackStackEntryAsState()
        val currentRoute = backStack?.destination?.route
        var signUpEmail by remember { mutableStateOf("") }
        var forgotPasswordEmail by remember { mutableStateOf("") }
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        val startDestination = when {
            !isOnboardingComplete -> OnboardingDestination.route
            !isLoggedIn -> AuthDestination.route
            else -> BlocksDestination.route
        }

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
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
                composable(
                    route = OnboardingDestination.route,
                    enterTransition = { authEnterTransition() },
                    exitTransition = { authExitTransition() },
                    popEnterTransition = { authPopEnterTransition() },
                    popExitTransition = { authPopExitTransition() },
                ) {
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

                composable(
                    route = AuthDestination.route,
                    enterTransition = { authEnterTransition() },
                    exitTransition = { authExitTransition() },
                    popEnterTransition = { authPopEnterTransition() },
                    popExitTransition = { authPopExitTransition() },
                ) {
                    AuthScreen(
                        onSignIn = { _, _ ->
                            navController.navigate(BlocksDestination.route) {
                                popUpTo(AuthDestination.route) { inclusive = true }
                            }
                        },
                        onSignUpClick = {
                            navController.navigate(SignUpDestination.route)
                        },
                        onGoogleAuth = {},
                        onForgotPassword = { email ->
                            forgotPasswordEmail = email
                            navController.navigate(ForgotPasswordDestination.route)
                        },
                    )
                }

                composable(
                    route = SignUpDestination.route,
                    enterTransition = { authEnterTransition() },
                    exitTransition = { authExitTransition() },
                    popEnterTransition = { authPopEnterTransition() },
                    popExitTransition = { authPopExitTransition() },
                ) {
                    SignUpScreen(
                        onSignUp = { email, _ ->
                            signUpEmail = email
                            navController.navigate(EmailVerificationDestination.route) {
                                popUpTo(SignUpDestination.route) { inclusive = true }
                            }
                        },
                        onBack = { navController.popBackStack() },
                        onForgotPassword = { email ->
                            forgotPasswordEmail = email
                            navController.navigate(ForgotPasswordDestination.route)
                        },
                        checkEmailAvailability = { email ->
                            if (email == "existing@tiik.app") {
                                SignUpEmailCheckResult.AlreadyExists
                            } else {
                                SignUpEmailCheckResult.Available
                            }
                        },
                    )
                }

                composable(
                    route = EmailVerificationDestination.route,
                    enterTransition = { authEnterTransition() },
                    exitTransition = { authExitTransition() },
                    popEnterTransition = { authPopEnterTransition() },
                    popExitTransition = { authPopExitTransition() },
                ) {
                    EmailVerificationScreen(
                        initialEmail = signUpEmail,
                        onContinue = {
                            navController.navigate(BlocksDestination.route) {
                                popUpTo(AuthDestination.route) { inclusive = true }
                            }
                        },
                        onBack = { navController.popBackStack() },
                        resendVerificationEmail = {},
                    )
                }

                composable(
                    route = ForgotPasswordDestination.route,
                    enterTransition = { authEnterTransition() },
                    exitTransition = { authExitTransition() },
                    popEnterTransition = { authPopEnterTransition() },
                    popExitTransition = { authPopExitTransition() },
                ) {
                    ForgotPasswordScreen(
                        initialEmail = forgotPasswordEmail,
                        onBackToSignIn = { navController.popBackStack() },
                        onCreateNewPassword = { email ->
                            forgotPasswordEmail = email
                            navController.navigate(CreateNewPasswordDestination.route)
                        },
                        onShowMessage = { message ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        },
                        requestPasswordReset = {},
                        checkResetPasswordStatus = { email ->
                            if (email == "confirmed@tiik.app") {
                                ForgotPasswordResetStatusResult.Confirmed
                            } else {
                                ForgotPasswordResetStatusResult.AwaitingConfirmation
                            }
                        },
                    )
                }

                composable(
                    route = CreateNewPasswordDestination.route,
                    enterTransition = { authEnterTransition() },
                    exitTransition = { authExitTransition() },
                    popEnterTransition = { authPopEnterTransition() },
                    popExitTransition = { authPopExitTransition() },
                ) {
                    CreateNewPasswordScreen(
                        initialEmail = forgotPasswordEmail,
                        onPasswordResetCompleted = {
                            navController.navigate(AuthDestination.route) {
                                popUpTo(AuthDestination.route) { inclusive = false }
                            }
                        },
                        onBack = { navController.popBackStack() },
                        updatePassword = { _, _ -> },
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
                        title = stringResource(Res.string.placeholder_title),
                        description = stringResource(Res.string.placeholder_navigation_description),
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

private fun AnimatedContentTransitionScope<*>.authEnterTransition(): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(AuthTransitionDurationMs),
        initialOffsetX = { fullWidth -> fullWidth },
    ) + fadeIn(animationSpec = tween(AuthTransitionDurationMs))
}

private fun AnimatedContentTransitionScope<*>.authExitTransition(): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(AuthTransitionDurationMs),
        targetOffsetX = { fullWidth -> -fullWidth / 3 },
    ) + fadeOut(animationSpec = tween(AuthTransitionDurationMs))
}

private fun AnimatedContentTransitionScope<*>.authPopEnterTransition(): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(AuthTransitionDurationMs),
        initialOffsetX = { fullWidth -> -fullWidth / 3 },
    ) + fadeIn(animationSpec = tween(AuthTransitionDurationMs))
}

private fun AnimatedContentTransitionScope<*>.authPopExitTransition(): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(AuthTransitionDurationMs),
        targetOffsetX = { fullWidth -> fullWidth },
    ) + fadeOut(animationSpec = tween(AuthTransitionDurationMs))
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
