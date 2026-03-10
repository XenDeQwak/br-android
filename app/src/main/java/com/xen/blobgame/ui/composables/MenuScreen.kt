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
    var joinRoomCode by remember { mutableStateOf("") }
    var createRoomName by remember { mutableStateOf("") }
    var maxPlayersExpanded by remember { mutableStateOf(false) }
    var selectedMaxPlayers by remember { mutableStateOf(4) }
    val player by playerViewModel.currentPlayer.collectAsState()
    val maxPlayersOptions = (2..10).toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        Text(
            text = "Join Room",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = joinRoomCode,
            onValueChange = { joinRoomCode = it },
            singleLine = true,
            label = { Text("Room Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (joinRoomCode.isNotBlank() && player != null) {
                    val request = GameRoomRequest(player!!.id)
                    gameRoomViewModel.joinRoom(joinRoomCode, request)
                    onRoomJoined()
                }
            },
            enabled = joinRoomCode.isNotBlank() && player != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Join Room")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(24.dp))

        // ==================== CREATE ROOM SECTION ====================
        Text(
            text = "Create Room",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = createRoomName,
            onValueChange = { createRoomName = it },
            singleLine = true,
            label = { Text("Room Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = maxPlayersExpanded,
            onExpandedChange = { maxPlayersExpanded = !maxPlayersExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedMaxPlayers.toString(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Max Players") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = maxPlayersExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
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

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (createRoomName.isNotBlank() && player != null) {
                    val request = GameRoomRequest(player!!.id)
                    gameRoomViewModel.createRoom(request, selectedMaxPlayers, createRoomName)
                    onRoomCreated()
                }
            },
            enabled = createRoomName.isNotBlank() && player != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Room")
        }
    }
}