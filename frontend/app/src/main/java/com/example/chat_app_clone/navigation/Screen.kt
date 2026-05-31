package com.example.chat_app_clone.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Chat : Screen("chat/{conversationId}/{userId}") {
        fun createRoute(conversationId: String, userId: String) = "chat/$conversationId/$userId"
    }
    object Profile : Screen("profile/{userId}") {
        fun createRoute(userId: String) = "profile/$userId"
    }
    object Search : Screen("search")
    object CreateGroup : Screen("create_group")
    object Calls : Screen("calls")
    object People : Screen("people")
    object Setting : Screen("Setting")
}
