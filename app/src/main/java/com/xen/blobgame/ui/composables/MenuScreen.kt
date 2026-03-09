package com.xen.blobgame.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xen.blobgame.data.remote.GameRoomRequest
import com.xen.blobgame.ui.viewmodel.GameRoomViewModel
import com.xen.blobgame.ui.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    gameRoomViewModel: GameRoomViewModel,
    playerViewModel: PlayerViewModel,
    onRoomJoined: () -> Unit,
    onRoomCreated: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var roomCode by remember { mutableStateOf("") }
    var maxPlayersExpanded by remember { mutableStateOf(false) }
    var selectedMaxPlayers by remember { mutableStateOf(4) }
    val player by playerViewModel.currentPlayer.collectAsState()

    val maxPlayersOptions = (4..10).toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Enter room name")
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            singleLine = true,
            label = { Text("Name") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Room Code (to join)")
        OutlinedTextField(
            value = roomCode,
            onValueChange = { roomCode = it },
            singleLine = true,
            label = { Text("Room Code") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = maxPlayersExpanded,
            onExpandedChange = { maxPlayersExpanded = !maxPlayersExpanded }
        ) {
            OutlinedTextField(
                value = selectedMaxPlayers.toString(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Max Players") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = maxPlayersExpanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = maxPlayersExpanded,
                onDismissRequest = { maxPlayersExpanded = false }
            ) {
                maxPlayersOptions.forEach { num ->
                    DropdownMenuItem(
                        text = { Text(num.toString()) },
                        onClick = {
                            selectedMaxPlayers = num
                            maxPlayersExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (roomCode.isNotBlank()) {
                    val request = GameRoomRequest(player!!.id)
                    gameRoomViewModel.joinRoom(roomCode, request)
                    onRoomJoined()
                }
            },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Join Room")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val request = GameRoomRequest(player!!.id)
                gameRoomViewModel.createRoom(selectedMaxPlayers, name, request)
                onRoomCreated()
            },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Room")
        }
    }
}