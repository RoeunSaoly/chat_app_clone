package com.example.chat_app_clone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chat_app_clone.MainActivity
import com.example.chat_app_clone.ui.Screens.HomesScreen
import com.example.chat_app_clone.ui.Screens.SettingsScreen
import com.example.chat_app_clone.ui.screens.*

import com.example.chat_app_clone.data.PreferenceManager

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Welcome.route,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val preferenceManager = remember { PreferenceManager(context) }
    val currentUserId = preferenceManager.getUserId()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
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
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // Home (chat list)
        composable(Screen.Home.route) {
            HomesScreen(
                onConversationClick = { conversation ->
                    val mine = currentUserId
                    val otherId = conversation.otherUser?.userId
                        ?: conversation.members.firstOrNull { it.userId != mine }?.userId
                        ?: 0L
                    navController.navigate(
                        Screen.Chat.createRoute(conversation.id.toString(), otherId.toString())
                    )
                },
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onCreateGroupClick = { },
                onCallsTabClick = { },
                onPeopleTabClick = { navController.navigate(Screen.People.route) },
                onSettingTabClick = { navController.navigate(Screen.Setting.route) },
                onNotificationsTapsClick = { navController.navigate(Screen.Notifications.route) },
            )
        }

        // People Screen
        composable(Screen.People.route) {
            PeopleScreen(
                onBack = { navController.popBackStack() },
                onChatClick = { userId ->
                    // For simplicity, navigate to chat with "0" as conversationId
                    // and let the ChatScreen handle finding/creating the conversation
                    navController.navigate(Screen.Chat.createRoute("0", userId.toString()))
                },
                onProfileClick = {},
                onHomeTabClick = {navController.navigate(Screen.Home.route)},
                onSettingTabClick = { navController.navigate(Screen.Setting.route) },
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onNotificationsTapsClick = { navController.navigate(Screen.Notifications.route) },
                )
        }

        // Setting Screen
        composable(Screen.Setting.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onHomeTabClick = {navController.navigate(Screen.Home.route)},
                onPeopleTabClick = { navController.navigate(Screen.People.route) },
                onNotificationsTapsClick = { navController.navigate(Screen.Notifications.route) },
                onLogoutClick = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Notifications Screen
        composable(Screen.Notifications.route) {
            NotificationScreen(
                onBack = { navController.popBackStack() },
                onHomeTabClick = {navController.navigate(Screen.Home.route)},
                onPeopleTabClick = { navController.navigate(Screen.People.route) },
                onSettingTabClick = { navController.navigate(Screen.Setting.route) },
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
                    val mine = currentUserId
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
