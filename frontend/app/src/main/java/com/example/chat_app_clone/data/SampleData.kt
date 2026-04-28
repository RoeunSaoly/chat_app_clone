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

    val users = emptyList<User>()
    val conversations = emptyList<Conversation>()
    val storyUsers = emptyList<User>()

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
