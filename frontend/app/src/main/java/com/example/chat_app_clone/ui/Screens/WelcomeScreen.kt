package com.example.chat_app_clone.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_app_clone.R
import com.example.chat_app_clone.ui.theme.MessengerBlue
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    // FIX 1: Explicitly define the type as List<Int> to solve "Cannot infer type"
    // FIX 2: Ensure welcome1, welcome2, welcome3 exist in res/drawable!
    val images: List<Int> = listOf(
        R.drawable.welcome1,
        R.drawable.welcome2,
        R.drawable.welcome3
    )

    val pagerState = rememberPagerState(pageCount = { images.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.weight(1f)) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Image(
                    painter = painterResource(id = images[page]),
                    contentDescription = "carousel image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Navigation Buttons
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    enabled = pagerState.currentPage > 0
                ) {
                    Text("Prev")
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    enabled = pagerState.currentPage < images.size - 1
                ) {
                    Text("Next")
                }
            }
        }

        // Page indicators
        Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(images.size) { iteration ->
                val color = if (pagerState.currentPage == iteration)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outlineVariant

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        // Welcome message and buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Messenger",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MessengerBlue,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Connect with friends and family",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Login Button
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MessengerBlue),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "Log In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Register Button
            OutlinedButton(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MessengerBlue
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}