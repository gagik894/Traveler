package com.together.traveler.ui.chat.chatsListScreen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.together.traveler.R
import com.together.traveler.model.ChatInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val chatsViewModel = viewModel<ChatsViewModel>()

    val uiState by chatsViewModel.uiState.collectAsState()

    LaunchedEffect(Unit){
        chatsViewModel.fetchData()
    }

    fun navigateToChat(chatItemId: String) {
        Log.i("chat", "navigateToChat: $chatItemId")
        navController.navigate("singleChatScreen/$chatItemId")
    }

    val scrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Chats") },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_action),
                        modifier = Modifier
                            .size(64.dp)
//                            .clickable(onClick = onNavIconPressed)
                            .padding(16.dp)
                    )
                }
            )
        },
        // Exclude ime and navigation bar padding so this can be added by the UserInput composable
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        if (uiState.isLoading) {
            Text(text = "Loading")
        } else {
            Log.i("asd", "onResponse: " + uiState.data)
            Column{
                Spacer(modifier = Modifier.height(5.dp))
                ChatsList(
                    modifier = Modifier.padding(paddingValues),
                    chats = uiState.data,
                    scrollState = scrollState,
                    navigateToChat = { chatItemId ->
                        navigateToChat(chatItemId)
                    }
                )
            }

        }
    }
}


@Composable
fun ChatsList(
    chats: List<ChatInfo>,
    modifier: Modifier = Modifier,
    navigateToChat: (String) -> Unit,
    scrollState: LazyListState
) {
    Log.i("asd", "onResponse: " + chats.size)
    LazyColumn(modifier = modifier, state = scrollState) {
        for (item in chats) {
            item {
                ChatItem(
                    chatItem = item,
                    navigateToChat = navigateToChat
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ChatItem(chatItem: ChatInfo, navigateToChat: (String) -> Unit) {
    val backgroundColor = if (chatItem.isUserHost) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }
    Surface(
        color = backgroundColor,
        modifier = Modifier.padding(horizontal = 10.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = { navigateToChat(chatItem._id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            GlideImage(
                model = "https://drive.google.com/uc?export=wiew&id=${chatItem.imgId}",
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = chatItem.title, fontWeight = FontWeight.Bold)
                Row {
                    chatItem.lastMessage?.let {
                        Text(
                            text = it.userId.username,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            fontSize = 14.sp
                        )
                        Text(text = ". ")
                        Box(modifier = Modifier.weight(1f)) {
                            Text(
                                text = it.content,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                fontSize = 14.sp
                            )
                        }
                        Text(text = " ")
//                    chatItem.lastMessage?.timestampString?.let {
//                        Text(
//                            text = it,
//                            overflow = TextOverflow.Ellipsis,
//                            maxLines = 1,
//                            fontSize = 14.sp
//                        )
//                    }
                    }

                }

            }
        }
    }
}

//@Preview
//@Composable
//fun ChatsListPreview() {
//    ChatTheme {
//        ChatsList(,{})
//    }
//}