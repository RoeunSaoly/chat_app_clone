package com.example.chat_app_clone.data

import com.example.chat_app_clone.data.model.Conversation
import com.example.chat_app_clone.data.model.ConversationMember
import com.example.chat_app_clone.data.model.Message
import com.example.chat_app_clone.data.model.User

object SampleData {
    val currentUser = User(
        id = 0L,
        username = "You",
        email = null,
        avatar = null,
        isOnline = true
    )

    val users = listOf(
        User(1L, "alice", "alice@example.com", null, true, null),
        User(2L, "bob", "bob@example.com", null, false, "2023-10-01T12:00:00Z")
    )
    val storyUsers = users

    val conversations = listOf(
        Conversation(
            id = 1L,
            type = "private",
            name = null,
            lastMessage = "Hey, how are you?",
            updatedAt = "2023-10-01T12:05:00Z",
            unreadCount = 2,
            members = listOf(
                memberFromUser(currentUser),
                memberFromUser(users[0])
            )
        ),
        Conversation(
            id = 2L,
            type = "private",
            name = null,
            lastMessage = "See you later!",
            updatedAt = "2023-10-01T10:00:00Z",
            unreadCount = 0,
            members = listOf(
                memberFromUser(currentUser),
                memberFromUser(users[1])
            )
        )
    )

    fun getMessagesForConversation(conversationId: String): List<Message> = emptyList()

    data class CallRecord(
        val user: User,
        val callType: String,
        val direction: String,
        val timestamp: String,
        val duration: String = ""
    )

    val callRecords = emptyList<CallRecord>()

    fun memberFromUser(user: User): ConversationMember {
        return ConversationMember(
            userId = user.id,
            username = user.username,
            avatar = user.avatar,
            isOnline = user.isOnline,
            lastSeen = user.lastSeen
        )
    }
}
