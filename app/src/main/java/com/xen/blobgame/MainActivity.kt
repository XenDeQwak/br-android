package com.xen.blobgame


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import com.xen.blobgame.data.remote.GameRoomApi
import com.xen.blobgame.data.remote.JoinRequest
import com.xen.blobgame.data.remote.PlayerApi
import com.xen.blobgame.data.repository.GameRoomRepository
import com.xen.blobgame.ui.composable.GameScreen
import com.xen.blobgame.ui.composable.MainMenuScreen
import com.xen.blobgame.ui.theme.BlobGameTheme
import com.xen.blobgame.ui.viewmodel.GameRoomViewModel
import com.xen.blobgame.ui.viewmodel.GameSessionViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private lateinit var gameViewModel: GameSessionViewModel
    private lateinit var gameRoomViewModel: GameRoomViewModel
    private var currentScreen = mutableStateOf("menu")
    private var currentPlayerId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val gameRoomApi = retrofit.create(GameRoomApi::class.java)
        val playerApi = retrofit.create(PlayerApi::class.java)
        val repository = GameRoomRepository(gameRoomApi)

        gameViewModel = ViewModelProvider(this)[GameSessionViewModel::class.java]

        val factory = GameRoomViewModel.Factory(repository)
        gameRoomViewModel = ViewModelProvider(this, factory)[GameRoomViewModel::class.java]

        setContent {
            BlobGameTheme {
                when (currentScreen.value) {
                    "menu" -> MainMenuScreen(
                        onJoinRoom = { roomId ->
                            val id = currentPlayerId ?: 123
                            gameRoomViewModel.joinRoom(roomId, JoinRequest(playerId = id))

                            currentScreen.value = "game"
                            gameViewModel.startSession(playerId = id, roomId = roomId)
                        },
                        onCreateRoom = { /* Implement create room logic */ },
                        onUpdateRoom = { /* Implement update room logic */ }
                    )
                    "game" -> GameScreen(gameViewModel)
                }
            }
        }
    }
}