package com.example.chat_app_clone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chat_app_clone.MainActivity
import com.example.chat_app_clone.ui.screens.*

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Welcome.route,
    modifier: Modifier = Modifier // ✅ added modifier parameter
) {
    val context = LocalContext.current
    val currentUserId = MainActivity.getCurrentUserId(context)

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier // ✅ forward modifier into NavHost
    ) {
        // Welcome screen
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }

        // Login screen
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        // Register screen
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // Home (chat list)
        composable(Screen.Home.route) {
            HomeScreen(
                onConversationClick = { conversation ->
                    val mine = currentUserId.toLongOrNull() ?: 0L
                    val otherId = conversation.otherUser?.userId
                        ?: conversation.members.firstOrNull { it.userId != mine }?.userId
                        ?: 0L
                    navController.navigate(
                        Screen.Chat.createRoute(conversation.id.toString(), otherId.toString())
                    )
                },
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onCreateGroupClick = { navController.navigate(Screen.CreateGroup.route) },
                onCallsTabClick = { navController.navigate(Screen.Calls.route) },
                onLogoutClick = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Chat screen
        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ChatScreen(
                conversationId = conversationId,
                userId = userId,
                currentUserId = currentUserId.toLongOrNull() ?: 0L,
                onBack = { navController.popBackStack() },
                onProfileClick = { navController.navigate(Screen.Profile.createRoute(userId)) }
            )
        }

        // Profile screen
        composable(
            route = Screen.Profile.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ProfileScreen(
                userId = userId,
                onBack = { navController.popBackStack() },
                onMessageClick = { navController.popBackStack() }
            )
        }

        // Search screen
        composable(Screen.Search.route) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onConversationCreated = { conversation ->
                    val mine = currentUserId.toLongOrNull() ?: 0L
                    val otherId = conversation.otherUser?.userId
                        ?: conversation.members.firstOrNull { it.userId != mine }?.userId
                        ?: 0L
                    navController.navigate(
                        Screen.Chat.createRoute(conversation.id.toString(), otherId.toString())
                    ) {
                        popUpTo(Screen.Search.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CreateGroup.route) {
            CreateGroupScreen(
                onBack = { navController.popBackStack() },
                onConversationCreated = { conversation ->
                    navController.navigate(Screen.Chat.createRoute(conversation.id.toString(), "0")) {
                        popUpTo(Screen.CreateGroup.route) { inclusive = true }
                    }
                }
            )
        }

        // Calls screen
        composable(Screen.Calls.route) {
            CallsScreen(onBack = { navController.popBackStack() })
        }
    }
}
