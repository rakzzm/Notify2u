package com.notify2u.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.notify2u.app.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val credentials = mapOf(
        "admin@notify2u.com" to Pair("Admin@Notify2u", "ADMIN"),
        "demo@notify2u.com" to Pair("DemoAccount123", "DEMO"),
        "user1@notify2u.com" to Pair("User@123", "USER"),
        "user2@notify2u.com" to Pair("User@123", "USER"),
        "user3@notify2u.com" to Pair("User@123", "USER"),
        "user4@notify2u.com" to Pair("User@123", "USER"),
        "user5@notify2u.com" to Pair("User@456", "USER")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Notify2u",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val cred = credentials[email.trim().lowercase()]
                if (cred != null && cred.first == password) {
                    val role = cred.second
                    viewModel.login(
                        email = email,
                        displayName = "${role.lowercase().replaceFirstChar { it.uppercase() }} Account",
                        role = role
                    )
                    onLoginSuccess()
                } else {
                    errorMessage = "Invalid email or password"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}
