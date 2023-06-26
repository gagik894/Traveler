package com.together.traveler.ui.chat.conversation

import androidx.compose.runtime.toMutableStateList
import com.together.traveler.model.Chat
import com.together.traveler.model.ChatInfo
import com.together.traveler.model.Message

class ConversationUiState(
    val channelName: String,
    initialMessages: List<Message>
) {
    private val _messages: MutableList<Message> = initialMessages.toMutableStateList()
    val messages: List<Message> = _messages

    fun addMessage(msg: Message) {
        _messages.add(0, msg) // Add to the beginning of the list
    }
}

class ChatsUiState(
    initialChats: List<ChatInfo>
) {
    private val _chats: MutableList<ChatInfo> = initialChats.toMutableStateList()
    val chats: List<ChatInfo> = _chats
}

class ChatUiState(
    val chat: Chat
) {
    private val _messages: MutableList<Message> = chat.messages.toMutableStateList()
    val messages: List<Message> = _messages

    fun addMessage(msg: Message) {
        _messages.add(0, msg) // Add to the beginning of the list
    }
}




