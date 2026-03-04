package com.xen.blobgame.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuScreen(
    onJoinRoom: (Int) -> Unit,
    onCreateRoom: (String) -> Unit,
    onUpdateRoom: (String) -> Unit
) {
    var roomId by remember { mutableStateOf("") }
    var roomName by remember { mutableStateOf("") }

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
            label = { Text("Room ID") }
        )

        Button(
            onClick = { roomId.toIntOrNull()?.let { onJoinRoom(it) } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Join Room")
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = roomName,
            onValueChange = { roomName = it },
            label = { Text("Room Name") }
        )

        Button(
            onClick = { onCreateRoom(roomName) },
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