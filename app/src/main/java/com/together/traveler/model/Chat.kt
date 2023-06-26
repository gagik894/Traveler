package com.together.traveler.model

import androidx.compose.runtime.Immutable
import com.together.traveler.R

@Immutable
data class Chat(
    val _id: String = "0",
    val title: String = "",
    val messages: List<Message> = emptyList(),
    val chat: List<Message> = emptyList(),
    val imageId: String = "",
    val imageUrl: Int = R.drawable.ali,
    val user: User = User()
)

