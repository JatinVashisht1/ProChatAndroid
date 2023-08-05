package com.example.demochatapplication.features.login.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.demochatapplication.core.CustomPaddingValues

/**
 * A reusable composable for rendering an OutlinedTextField for the login screen.
 *
 * @param value The current value of the text field.
 * @param modifier The modifier for the text field.
 * @param placeholder A composable function to display the placeholder text when the text field is empty.
 * @param label A composable function to display the label for the text field.
 * @param onValueChange A callback function to handle changes to the text field value.
 */
@Composable
fun LoginScreenTextFieldComposable(
    value: String,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit,
    label: @Composable () -> Unit,
    onValueChange: (newValue: String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth().padding(horizontal = CustomPaddingValues.SMALL),
        placeholder = placeholder,
        label = label,
    )
}
