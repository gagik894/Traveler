package com.together.traveler.ui.chat

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.together.traveler.context.AppContext
import com.together.traveler.ui.chat.chatsListScreen.ChatsScreen
import com.together.traveler.ui.chat.singleChatScreen.SingleChatScreen
import com.together.traveler.ui.chat.theme.ChatTheme
import com.together.traveler.ui.main.MainActivity

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppContext.init(this)
        supportActionBar?.hide()
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        fun navigateToHome() {
            startActivity(Intent(this, MainActivity::class.java))
        }

        setContent {
            ChatTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "chatsScreen"
                ) {
                    composable("chatsScreen") {
                        ChatsScreen(navController, Modifier, ::navigateToHome)
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


