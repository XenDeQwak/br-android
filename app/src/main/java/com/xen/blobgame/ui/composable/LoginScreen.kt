package com.xen.blobgame.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xen.blobgame.ui.viewmodel.PlayerViewModel

@Composable
fun LoginScreen(
    viewModel: PlayerViewModel,
    onPlayerCreated: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val currentPlayer by viewModel.currentPlayer.collectAsState()

    LaunchedEffect(currentPlayer) {
        if (currentPlayer != null) {
            onPlayerCreated()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Enter your name", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.createPlayer(name) },
            enabled = name.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Start Game")
            }
        }
    }
}
