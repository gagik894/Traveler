package com.together.traveler.ui.chat

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.together.traveler.context.AppContext
import com.together.traveler.ui.chat.chatsListScreen.ChatsScreen
import com.together.traveler.ui.chat.singleChatScreen.SingleChatScreen
import com.together.traveler.ui.chat.theme.ChatTheme

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppContext.init(this)
        supportActionBar?.hide()
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ChatTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "chatsScreen"
                ) {
                    composable("chatsScreen") {
                        ChatsScreen(navController)
                    }
                    composable("singleChatScreen/{chatItemId}") { backStackEntry ->
                        val chatItemId = backStackEntry.arguments?.getString("chatItemId")
                        SingleChatScreen(
                            _id = chatItemId,
                            navigateUp = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}


