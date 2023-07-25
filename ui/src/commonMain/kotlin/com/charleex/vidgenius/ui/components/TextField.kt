package com.charleex.vidgenius.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Suppress("FunctionName")
@ExperimentalMaterialApi
@Composable
internal fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    title: String = "",
    leadingIcon: ImageVector? = null,
    isSecure: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true,
) {
    var textValue by remember { mutableStateOf(value) }

    LaunchedEffect(textValue) {
        onValueChange(textValue)
    }

    val focusRequester = FocusRequester()
    var focusState by remember { mutableStateOf(false) }
    var visualTransformationState by remember {
        mutableStateOf(
            if (isSecure) PasswordVisualTransformation() else VisualTransformation.None
        )
    }

    val visualTransformationIcon =
        if (visualTransformationState is PasswordVisualTransformation)
            Icons.Filled.Visibility else Icons.Filled.VisibilityOff

    val scaleState by animateFloatAsState(
        targetValue = if (focusState) 1.02f else 1f,
        animationSpec = tween(500)
    )
    val backgroundColorState by animateColorAsState(
        targetValue = if (focusState) MaterialTheme.colors.surface else MaterialTheme.colors.surface.copy(alpha = .5f),
        animationSpec = tween(500)
    )
    val fontWeightState by animateIntAsState(
        targetValue = if (focusState) 700 else 500,
        animationSpec = tween(500)
    )

    Column(

    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight(fontWeightState),
            color = MaterialTheme.colors.onSurface.copy(alpha = .5f),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = textValue,
            onValueChange = { textValue = it },
            enabled = enabled,
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier
                    )
                }
            },
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(isSecure && textValue.isNotEmpty()) {
                        Icon(
                            imageVector = visualTransformationIcon,
                            contentDescription = "Toggle password visibility",
                            modifier = Modifier
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null,
                                    onClick = {
                                        visualTransformationState = visualTransformationState.toggle()
                                    },
                                )
                        )
                    }
                    AnimatedVisibility(
                        textValue.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear text input",
                            modifier = Modifier
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null,
                                    onClick = {
                                        textValue = ""
                                        onValueChange("")
                                    }
                                )
                        )
                    }
                }
            },
            shape = MaterialTheme.shapes.small,
            singleLine = true,
            maxLines = 1,
            visualTransformation = visualTransformationState,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            textStyle = MaterialTheme.typography.body2,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = backgroundColorState,
                placeholderColor = MaterialTheme.colors.onSurface.copy(alpha = .5f),
                leadingIconColor = MaterialTheme.colors.onSurface,
                trailingIconColor = MaterialTheme.colors.onSurface,
                textColor = MaterialTheme.colors.onSurface,
                cursorColor = MaterialTheme.colors.secondary,

                ),
            modifier = modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState = it.isFocused }
                .graphicsLayer {
                    scaleX = scaleState
                    scaleY = scaleState
                }
        )
    }
}

private fun VisualTransformation.toggle() = if (this is PasswordVisualTransformation) {
    VisualTransformation.None
} else {
    PasswordVisualTransformation()
}
