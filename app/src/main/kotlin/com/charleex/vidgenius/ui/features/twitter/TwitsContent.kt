package com.charleex.vidgenius.ui.features.twitter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.twitter.createNewTweet
import com.charleex.vidgenius.ui.components.list.ListHeaderTwitter

@Composable
internal fun TwitsContent(

) {
    var value by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                PaddingValues(
                    top = 100.dp,
                    start = 32.dp,
                    end = 32.dp,
                    bottom = 32.dp
                )
            )
    ) {
        ListHeaderTwitter(
            title = "Tweets",
            count = 0, // tweets.size,
            isRefreshing = false,
            startRefresh = {
                // Get tweets
            },
            stopRefresh = {
                // Stop getting tweets
            },
        )
        Column(
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text("Tweet") },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(500.dp)
                    .height(200.dp)
            )
            Button(
                onClick = {
                    createNewTweet()
                },
                modifier = Modifier.align(Alignment.End)
                    .padding(top = 12.dp)
            ) {
                Text(text = "Create new tweet")
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
//                Column(
//                    verticalArrangement = Arrangement.spacedBy(8.dp),
//                ) {
//                }
            }
        }
    }
}
