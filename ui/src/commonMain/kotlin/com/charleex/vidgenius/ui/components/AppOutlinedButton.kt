package com.charleex.vidgenius.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
internal fun AppOutlinedButton(
    label: String,
    icon: ImageVector? = null,
    bgColor: Color = MaterialTheme.colors.surface,
    labelColor: Color = MaterialTheme.colors.onSurface,
    onClick: () -> Unit,
) {
    OutlinedButton(
        shape = RoundedCornerShape(10.dp),
        elevation = null,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colors.primary,
        ),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = bgColor,
        ),
        onClick = onClick,
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.button,
            color = labelColor,
        )
    }
}
