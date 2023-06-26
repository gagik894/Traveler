
@file:OptIn(ExperimentalMaterial3Api::class)

package com.together.traveler.ui.chat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.together.traveler.ui.chat.theme.ChatTheme
import com.together.traveler.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        actions = actions,
        title = title,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
//            Button(onClick = { onNavIconPressed }) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_action),
                modifier = Modifier
                    .size(64.dp)
                    .clickable(onClick = onNavIconPressed)
                    .padding(16.dp)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ChatAppBarPreview() {
    ChatTheme {
        ChatAppBar(title = { Text("Preview!") })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ChatAppBarPreviewDark() {
    ChatTheme(isDarkTheme = true) {
        ChatAppBar(
            title = { Text("AI conf Armenia 2023: From start to end") },
            actions = {

                // Info icon
                Icon(
                    imageVector = Icons.Outlined.Info,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .height(24.dp),
                    contentDescription = stringResource(id = R.string.info)
                )
            }
        )
    }
}
