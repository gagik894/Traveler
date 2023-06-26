package com.together.traveler.model

import android.annotation.SuppressLint
import androidx.compose.runtime.Immutable
import com.together.traveler.R
import java.text.SimpleDateFormat
import java.util.Date

@Immutable
data class Message(
    val author: String = "",
    val content: String,
    val timestamp: Date,
    val userId: User,
    val image: Int? = null,
    val authorImage: Int = if (author == "me") R.drawable.ali else R.drawable.someone_else
){
    val timestampString: String
        get() = formatDateTime(timestamp)
}

@SuppressLint("SimpleDateFormat")
fun formatDateTime(date: Date): String {
    val now = Date()
    val isToday = SimpleDateFormat("yyyyMMdd").format(date) == SimpleDateFormat("yyyyMMdd").format(now)

    return if (isToday) {
        SimpleDateFormat("h:mm a").format(date) // Format for time if it's today
    } else {
        SimpleDateFormat("MMM dd, yyyy").format(date) // Format for date if it's not today
    }
}