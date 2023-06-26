package com.together.traveler.ui.chat.data


import com.together.traveler.ui.chat.data.EMOJIS.EMOJI_CLOUDS
import com.together.traveler.ui.chat.data.EMOJIS.EMOJI_FLAMINGO
import com.together.traveler.ui.chat.data.EMOJIS.EMOJI_MELTING
import com.together.traveler.ui.chat.data.EMOJIS.EMOJI_PINK_HEART
import com.together.traveler.ui.chat.data.EMOJIS.EMOJI_POINTS
import com.together.traveler.R
import com.together.traveler.model.Chat
import com.together.traveler.model.ChatInfo
import com.together.traveler.model.Message
import com.together.traveler.model.User
import com.together.traveler.ui.chat.conversation.*
import java.util.Date

private val mockChatsInfo: List<ChatInfo> = listOf(
    ChatInfo(
        _id = "1",
        title = "Chat 1",
        lastMessage = Message(
            "me",
            "Check it out!",
            Date(),
            userId = User(),
        ),
        imgId  = ""
    ),
    ChatInfo(
        _id = "2",
        title = "Chat 2",
        lastMessage = Message(
            "me",
            "Check it out!",
            Date(),
            userId = User(),
        ),
        imgId  = ""
    ),
    ChatInfo(
        _id = "3",
        title = "Chat 3",
        lastMessage = Message(
            "me",
            "Check it out!",
            Date(),
            userId = User(),
        ),
        imgId  = ""
    ),
    ChatInfo(
        _id = "4",
        title = "Chat 4",
        lastMessage = Message(
            "me",
            "Check it out!",
            Date(),
            userId = User(),
        ),
        imgId  = ""
    ),
    ChatInfo(
        _id = "5",
        title = "Chat 5",
        lastMessage = Message(
            "me",
            "Check it out!",
            Date(),
            userId = User(),
        ),
        imgId  = ""
    ),
    ChatInfo(
        _id = "6",
        title = "Chat 6",
        lastMessage = Message(
            "me",
            "Check it out!",
            Date(),
            userId = User(),
        ),
        imgId  = ""
    ),
)



private val initialMessages = listOf(
    Message(
        "me",
        "Check it out!",
        Date(),
        userId = User(),
    ),
    Message(
        "me",
        "Thank you!$EMOJI_PINK_HEART",
        Date(),
        userId = User(),
        R.drawable.sticker
    ),
    Message(
        "Taylor Brooks",
        "You can use all the same stuff",
        Date(),
        userId = User(),
    ),
    Message(
        "Taylor Brooks",
        "@aliconors Take a look at the `Flow.collectAsStateWithLifecycle()` APIs",
        Date(),
        userId = User(),
    ),
    Message(
        "John Glenn",
        "Compose newbie as well $EMOJI_FLAMINGO, have you looked at the JetNews sample? " +
            "Most blog posts end up out of date pretty fast but this sample is always up to " +
            "date and deals with async data loading (it's faked but the same idea " +
            "applies) $EMOJI_POINTS https://goo.gle/jetnews",
        Date(),
        userId = User(),
    ),
    Message(
        "me",
        "Compose newbie: I’ve scourged the internet for tutorials about async data " +
            "loading but haven’t found any good ones $EMOJI_MELTING $EMOJI_CLOUDS. " +
            "What’s the recommended way to load async data and emit composable widgets?",
        Date(),
        userId = User(),
    )
)


val exampleUiState = ConversationUiState(
    channelName = "#composers",
    initialMessages = initialMessages
)

val exampleChatUiState = ChatUiState(
    Chat(
        _id = "2",
        title = "Chat 2",
        chat = initialMessages,
        messages = initialMessages,
        imageId  = "",
    )
)

val exampleChatsUiState = ChatsUiState(
    initialChats = mockChatsInfo
)

object EMOJIS {
    // EMOJI 15
    const val EMOJI_PINK_HEART = "\uD83E\uDE77"

    // EMOJI 14 🫠
    const val EMOJI_MELTING = "\uD83E\uDEE0"

    // ANDROID 13.1 😶‍🌫️
    const val EMOJI_CLOUDS = "\uD83D\uDE36\u200D\uD83C\uDF2B️"

    // ANDROID 12.0 🦩
    const val EMOJI_FLAMINGO = "\uD83E\uDDA9"

    // ANDROID 12.0  👉
    const val EMOJI_POINTS = " \uD83D\uDC49"
}
