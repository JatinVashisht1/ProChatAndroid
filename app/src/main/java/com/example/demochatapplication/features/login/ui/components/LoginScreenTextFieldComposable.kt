package com.example.demochatapplication.features.login.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

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
fun UsernameTextFieldComposable(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: @Composable () -> Unit,
    label: @Composable () -> Unit,
    onValueChange: (newValue: String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        label = label,
    )
}

@Composable
fun PasswordTextFieldComposable(
    modifier: Modifier = Modifier,
    showPassword: Boolean = false,
    password: String = "",
    placeholder: @Composable () -> Unit,
    label: @Composable () -> Unit,
    onPasswordValueChange: (newPasswordValue: String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier,
        value = password,
        onValueChange = onPasswordValueChange,
        placeholder = placeholder,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = if (showPassword) KeyboardType.Text else KeyboardType.Password),
        visualTransformation = PasswordVisualTransformation('*')
    )
}