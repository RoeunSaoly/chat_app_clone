package com.example.chat_app_clone.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_app_clone.MainActivity
import com.example.chat_app_clone.network.AuthService
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_app_clone.viewmodel.LoginViewModel
import com.example.chat_app_clone.network.RetrofitClient
import com.example.chat_app_clone.network.SocketManager
import com.example.chat_app_clone.ui.theme.MessengerBlue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Handle successful login
    LaunchedEffect(uiState.authResponse) {
        uiState.authResponse?.let { response ->
            if (response.success && response.accessToken != null) {
                com.example.chat_app_clone.MainActivity.saveToken(context, response.accessToken, response.refreshToken ?: "")
                response.user?.let { user ->
                    com.example.chat_app_clone.MainActivity.saveCurrentUserId(context, user.id, user.username ?: "")
                }
                com.example.chat_app_clone.network.RetrofitClient.setAuthToken(response.accessToken)
                com.example.chat_app_clone.network.SocketManager.getInstance().connectSocket(response.accessToken)
                
                // Get FCM Token and send to server
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("LoginScreen", "Fetching FCM registration token failed", task.exception)
                        return@addOnCompleteListener
                    }
                    val token = task.result
                    com.example.chat_app_clone.data.PreferenceManager(context).saveFcmToken(token)
                    kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                        try {
                            RetrofitClient.userApi.updateProfile(com.example.chat_app_clone.network.UpdateProfileRequest(fcmToken = token))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                
                onLoginSuccess()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo
        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = "Messenger Logo",
            modifier = Modifier.size(80.dp),
            tint = MessengerBlue
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Welcome to Messenger",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MessengerBlue
        )
        
        Text(
            "Log in to your account",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(image, contentDescription = null)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        // Error message
        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MessengerBlue),
            enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToRegister,
            enabled = !uiState.isLoading
        ) {
            Text("Don't have an account? Sign Up", color = MessengerBlue)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
