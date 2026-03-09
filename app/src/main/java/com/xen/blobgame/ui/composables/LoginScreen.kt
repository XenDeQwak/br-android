package com.xen.blobgame.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xen.blobgame.ui.viewmodel.PlayerViewModel

@Composable
fun LoginScreen(
    playerViewModel: PlayerViewModel,
    onPlayerCreated: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    val currentPlayer by playerViewModel.currentPlayer.collectAsState()

    LaunchedEffect(currentPlayer) {
        if (currentPlayer != null) {
            onPlayerCreated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Enter Your Name")

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            singleLine = true,
            label = { Text("Player Name") }
        )

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    playerViewModel.createPlayer(name)
                }
            },
            enabled = name.isNotBlank(),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Login")
        }
    }
}