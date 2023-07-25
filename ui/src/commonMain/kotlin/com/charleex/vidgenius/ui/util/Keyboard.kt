package com.charleex.vidgenius.ui.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.SoftwareKeyboardController

@ExperimentalComposeUiApi
internal fun clearFocus(
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
) {
    keyboardController?.hide()
    focusManager.clearFocus()
}
