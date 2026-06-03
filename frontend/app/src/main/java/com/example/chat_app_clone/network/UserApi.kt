package com.example.chat_app_clone.network

import com.example.chat_app_clone.network.model.GenericApiResponse
import com.example.chat_app_clone.network.model.UserResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @GET("api/users")
    suspend fun searchUsers(@Query("search") search: String? = null): Response<UsersApiResponse>

    @GET("api/users/profile")
    suspend fun getProfile(): Response<UserProfileResponse>

    @PUT("api/users/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserProfileResponse>

    // Friend related endpoints
    @GET("api/friends/recommended")
    suspend fun getRecommendedFriends(): Response<UsersApiResponse>

    @GET("api/friends/requests")
    suspend fun getFriendRequests(): Response<UsersApiResponse>

    @GET("api/friends")
    suspend fun getFriends(): Response<UsersApiResponse>

    @POST("api/friends/request/{userId}")
    suspend fun sendFriendRequest(@Path("userId") userId: Long): Response<GenericApiResponse>

    @POST("api/friends/accept/{userId}")
    suspend fun acceptFriendRequest(@Path("userId") userId: Long): Response<GenericApiResponse>

    @POST("api/friends/reject/{userId}")
    suspend fun rejectFriendRequest(@Path("userId") userId: Long): Response<GenericApiResponse>

    @DELETE("api/friends/{userId}")
    suspend fun unfriend(@Path("userId") userId: Long): Response<GenericApiResponse>

    // Notification related endpoints
    @GET("api/notifications")
    suspend fun getNotifications(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<NotificationsApiResponse>

    @PATCH("api/notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: Long): Response<GenericApiResponse>

    @PATCH("api/notifications/read-all")
    suspend fun markAllNotificationsRead(): Response<GenericApiResponse>

    @GET("api/notifications/unread-count")
    suspend fun getUnreadNotificationCount(): Response<UnreadCountResponse>
}

data class NotificationsApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: NotificationsData? = null,
    @SerializedName("error") val error: String? = null
)

data class NotificationsData(
    @SerializedName("notifications") val notifications: List<NotificationResponse>,
    @SerializedName("pagination") val pagination: PaginationData,
    @SerializedName("unreadCount") val unreadCount: Int
)

data class NotificationResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("related_id") val relatedId: Long?,
    @SerializedName("created_at") val createdAt: String
)

data class PaginationData(
    @SerializedName("total") val total: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("offset") val offset: Int,
    @SerializedName("hasMore") val hasMore: Boolean
)

data class UnreadCountResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: UnreadCountData? = null,
    @SerializedName("error") val error: String? = null
)

data class UnreadCountData(
    @SerializedName("count") val count: Int
)

data class UsersApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<UserResponse>? = null,
    @SerializedName("error") val error: String? = null
)

data class UserProfileResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: UserResponse,
    @SerializedName("error") val error: String? = null
)

data class UpdateProfileRequest(
    @SerializedName("username") val username: String? = null,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("fcm_token") val fcmToken: String? = null
)
