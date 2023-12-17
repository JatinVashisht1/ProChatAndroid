package com.example.demochatapplication.features.authentication.ui.login.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoginButtonComposable(
    modifier: Modifier = Modifier,
    onLoginButtonClicked: () -> Unit = {},
) {
    Button(onClick = onLoginButtonClicked, modifier = modifier) {
        Text(text = "Login")
    }
}

@Composable
@Preview
fun PreviewLoginButtonComposable() {
    LoginButtonComposable()
}