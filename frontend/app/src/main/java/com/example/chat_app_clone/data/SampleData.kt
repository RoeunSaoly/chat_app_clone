package com.example.chat_app_clone.data

import com.example.chat_app_clone.data.model.Conversation
import com.example.chat_app_clone.data.model.Message
import com.example.chat_app_clone.data.model.MessageStatus
import com.example.chat_app_clone.data.model.User

object SampleData {

    val currentUser = User(
        id = "me",
        name = "You",
        username = "yourhandle",
        isOnline = true
    )

    val users = listOf(
        User("1", "Sophia Carter", isOnline = true, username = "sophiacarter"),
        User("2", "James Liu", isOnline = false, lastSeen = "5m ago", username = "jamesliu"),
        User("3", "Mia Nguyen", isOnline = true, username = "mia.nguyen"),
        User("4", "Ethan Brooks", isOnline = false, lastSeen = "1h ago", username = "ethanbrooks"),
        User("5", "Aria Patel", isOnline = true, username = "ariapatel"),
        User("6", "Liam Johnson", isOnline = false, lastSeen = "Yesterday", username = "liamj"),
        User("7", "Emma Wilson", isOnline = true, username = "emma.wilson"),
        User("8", "Noah Kim", isOnline = false, lastSeen = "2d ago", username = "noahkim"),
        User("9", "Olivia Chen", isOnline = true, username = "olivia.chen"),
        User("10", "Aiden Scott", isOnline = false, lastSeen = "3d ago", username = "aidensco"),
    )

    val conversations = listOf(
        Conversation("c1", users[0], "😂 You sent a photo", "Just now", unreadCount = 3, isPinned = true),
        Conversation("c2", users[1], "See you tomorrow!", "2m", unreadCount = 1),
        Conversation("c3", users[2], "Sounds good to me 👍", "15m", unreadCount = 0),
        Conversation("c4", users[3], "Did you watch the game last night?", "1h", unreadCount = 5),
        Conversation("c5", users[4], "Thanks for the help!", "2h", unreadCount = 0),
        Conversation("c6", users[5], "Let me know when you're free", "Yesterday", unreadCount = 0),
        Conversation("c7", users[6], "The meeting is at 3pm", "Yesterday", unreadCount = 2),
        Conversation("c8", users[7], "Miss you! 💙", "Mon", unreadCount = 0),
        Conversation("c9", users[8], "That was so fun!", "Sun", unreadCount = 0),
        Conversation("c10", users[9], "Happy Birthday! 🎉", "Sat", unreadCount = 0),
    )

    val storyUsers = listOf(users[0], users[2], users[4], users[6], users[8])

    fun getMessagesForConversation(conversationId: String): List<Message> {
        return when (conversationId) {
            "c1" -> listOf(
                Message("m1", "1", "Hey! How are you doing? 😊", "10:00 AM", MessageStatus.READ),
                Message("m2", "me", "I'm great, thanks! What about you?", "10:02 AM", MessageStatus.READ),
                Message("m3", "1", "Doing amazing! Did you see the new season?", "10:05 AM", MessageStatus.READ),
                Message("m4", "me", "Not yet! No spoilers 😂", "10:06 AM", MessageStatus.READ),
                Message("m5", "1", "Haha okay okay, you need to watch it ASAP though!", "10:07 AM", MessageStatus.READ),
                Message("m6", "me", "Adding it to my list right now lol", "10:09 AM", MessageStatus.READ),
                Message("m7", "1", "😂 You sent a photo", "10:10 AM", MessageStatus.DELIVERED),
            )
            "c2" -> listOf(
                Message("m1", "2", "Are we still on for tomorrow?", "Yesterday", MessageStatus.READ),
                Message("m2", "me", "Yes! Looking forward to it", "Yesterday", MessageStatus.READ),
                Message("m3", "2", "Great! 7pm works for you?", "Yesterday", MessageStatus.READ),
                Message("m4", "me", "Perfect, see you then!", "Yesterday", MessageStatus.READ),
                Message("m5", "2", "See you tomorrow!", "2m", MessageStatus.DELIVERED),
            )
            else -> listOf(
                Message("m1", "3", "Hey there! 👋", "Mon", MessageStatus.READ),
                Message("m2", "me", "Hey! What's up?", "Mon", MessageStatus.READ),
                Message("m3", "3", "Not much, just checking in 😊", "Mon", MessageStatus.READ),
                Message("m4", "me", "That's sweet! Everything's good here", "Mon", MessageStatus.READ),
                Message("m5", "3", "Sounds good to me 👍", "15m", MessageStatus.DELIVERED),
            )
        }
    }

    data class CallRecord(
        val user: User,
        val callType: String, // "audio" or "video"
        val direction: String, // "incoming" or "outgoing" or "missed"
        val timestamp: String,
        val duration: String = ""
    )

    val callRecords = listOf(
        CallRecord(users[0], "video", "incoming", "Today, 10:30 AM", "12:45"),
        CallRecord(users[2], "audio", "outgoing", "Today, 9:15 AM", "5:22"),
        CallRecord(users[4], "video", "missed", "Yesterday, 8:00 PM"),
        CallRecord(users[1], "audio", "incoming", "Yesterday, 3:45 PM", "2:10"),
        CallRecord(users[6], "audio", "outgoing", "Mon, 7:30 PM", "18:33"),
        CallRecord(users[3], "video", "missed", "Sun, 11:00 AM"),
        CallRecord(users[8], "audio", "incoming", "Sat, 6:15 PM", "45:02"),
        CallRecord(users[5], "video", "outgoing", "Fri, 2:00 PM", "8:17"),
    )
}
