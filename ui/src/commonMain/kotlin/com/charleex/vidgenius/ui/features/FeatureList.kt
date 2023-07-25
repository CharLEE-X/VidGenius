package com.charleex.vidgenius.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.ui.components.AppCard

@Composable
internal fun FeatureList(
    onGotToLogin: () -> Unit,
    onGoToDragDrop: () -> Unit,
    onGoToVideoList: () -> Unit,
    onGotToVideoDetail: (id: String) -> Unit,
) {
    AppCard(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "Feature List",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Button(
                onClick = onGotToLogin
            ) {
                Text("Login")
            }
            Button(
                onClick = onGoToDragDrop
            ) {
                Text("Drag Drop")
            }
            Button(
                onClick = onGoToVideoList
            ) {
                Text("Video List")
            }
            Button(
                onClick = {
                    val id = "iza8USq42Vo"
                    onGotToVideoDetail(id)
                }
            ) {
                Text("Video Detail")
            }
        }
    }
}
