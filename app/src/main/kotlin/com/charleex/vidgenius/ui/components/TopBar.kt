package com.charleex.vidgenius.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CarCrash
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    categories: Map<String, ImageVector>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
) {
    val categorySegments = categories.map { (name, icon) ->
        SegmentSpec(
            icon = icon,
            label = name,
            colors = SegmentDefaults.defaultColors(),
            corners = SegmentDefaults.defaultCorners(),
        )
    }
    val selectedIndex = categorySegments.indexOfFirst { it.label == selectedCategory }
    val onSelected = { index: Int ->
        onCategorySelected(categorySegments[index].label)
    }

    Surface(
        tonalElevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 64.dp)
        ) {
            Text(
                text = "VIDGENIUS",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.weight(1f))
            SegmentsGroup(
                segments = categorySegments,
                selectedIndex = selectedIndex,
                onSegmentSelected = onSelected,
                modifier = Modifier
                    .width(500.dp)
                    .padding(horizontal = 32.dp),
            )
        }
    }
}


@Preview
@Composable
fun TopBarPreview() {
    Surface(
        tonalElevation = 0.dp,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
//                .fillMaxSize()
        ) {
            PreviewItem(
                darkTheme = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                TopBar(
                    categories = mapOf(
                        "Home" to Icons.Default.Home,
                        "Pets" to Icons.Default.Pets,
                        "CarCrash" to Icons.Default.CarCrash,
                        "ArrowBack" to Icons.Default.ArrowBack,
                        "ArrowForward" to Icons.Default.ArrowForward,
                    ),
                    selectedCategory = "Home",
                    onCategorySelected = {},
                )
            }
            PreviewItem(
                darkTheme = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                TopBar(
                    categories = mapOf(
                        "Home" to Icons.Default.Home,
                        "Pets" to Icons.Default.Pets,
                        "CarCrash" to Icons.Default.CarCrash,
                        "ArrowBack" to Icons.Default.ArrowBack,
                        "ArrowForward" to Icons.Default.ArrowForward,
                    ),
                    selectedCategory = "Pets",
                    onCategorySelected = {},
                )
            }
        }
    }
}

@Composable
private fun PreviewItem(
    modifier: Modifier,
    darkTheme: Boolean,
    block: @Composable () -> Unit,
) {
    AppTheme(
        darkTheme = darkTheme
    ) {
        Surface(
            modifier = modifier
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
//                    .fillMaxSize()
            ) {
                block()
            }
        }
    }
}
