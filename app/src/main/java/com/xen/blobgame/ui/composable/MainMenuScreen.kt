package com.xen.blobgame.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuScreen(
    onJoinRoom: (Int) -> Unit,
    onCreateRoom: (String, Int) -> Unit,
    onUpdateRoom: (String) -> Unit
) {
    var roomId by remember { mutableStateOf("") }
    var roomName by remember { mutableStateOf("") }
    var maxPlayers by remember { mutableStateOf("4") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Main Menu", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = roomId,
            onValueChange = { roomId = it },
            label = { Text("Room ID") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { roomId.toIntOrNull()?.let { onJoinRoom(it) } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Join Room")
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = roomName,
            onValueChange = { roomName = it },
            label = { Text("Room Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = maxPlayers,
            onValueChange = { maxPlayers = it },
            label = { Text("Max Players") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { 
                val players = maxPlayers.toIntOrNull() ?: 4
                onCreateRoom(roomName, players) 
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Room")
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { onUpdateRoom(roomName) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Room")
        }
    }
}