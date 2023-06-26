package com.together.traveler.model

import androidx.compose.runtime.Immutable
import com.together.traveler.R

@Immutable
data class ChatInfo(
    val _id: String,
    val title: String,
    val lastMessage: Message?,
    val imageUrl: Int = R.drawable.ali,
    val imgId: String,
    val isUserHost: Boolean = false
)