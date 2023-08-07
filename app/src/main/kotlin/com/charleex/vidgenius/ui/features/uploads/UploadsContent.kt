package com.charleex.vidgenius.ui.features.uploads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AnimalsUploadsContent(

) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(text = "Animals")
            UploadsContent()
        }
    }
}

@Composable
fun FailsUploadsContent(
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(text = "Fails")
            UploadsContent()
        }
    }
}

@Composable
fun UploadsContent(
    items: List<String> = listOf("1", "2", "3")
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            Text(text = "Uploads")
        }
        items(items) {
            Text(text = it)
        }
    }
}
