package com.xen.blobgame


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.xen.blobgame.data.remote.GameRoomApi
import com.xen.blobgame.data.remote.JoinRequest
import com.xen.blobgame.data.remote.NewPlayer
import com.xen.blobgame.data.remote.PlayerApi
import com.xen.blobgame.data.remote.RoomRequest
import com.xen.blobgame.data.repository.GameRoomRepository
import com.xen.blobgame.ui.composable.GameScreen
import com.xen.blobgame.ui.composable.MainMenuScreen
import com.xen.blobgame.ui.theme.BlobGameTheme
import com.xen.blobgame.ui.viewmodel.GameRoomViewModel
import com.xen.blobgame.ui.viewmodel.GameSessionViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {

    private lateinit var gameViewModel: GameSessionViewModel
    private lateinit var gameRoomViewModel: GameRoomViewModel
    private var currentScreen = mutableStateOf("menu")
    private var currentPlayerId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, JsonSerializer<LocalDateTime> { src, _, _ ->
                JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            })
            .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer { json, _, _ ->
                LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            })
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")
            .addConverterFactory(GsonConverterFactory.create(gson))
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
                            lifecycleScope.launch {
                                try {
                                    val id = currentPlayerId ?: createNewPlayer(playerApi)
                                    currentPlayerId = id

                                    gameRoomViewModel.joinRoom(roomId, JoinRequest(playerId = id))

                                    currentScreen.value = "game"
                                    gameViewModel.startSession(playerId = id, roomId = roomId)
                                    gameViewModel.connect()
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Error joining room", e)
                                    Toast.makeText(this@MainActivity, "Failed to join room: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        onCreateRoom = { roomName ->
                            lifecycleScope.launch {
                                try {
                                    val id = currentPlayerId ?: createNewPlayer(playerApi)
                                    currentPlayerId = id

                                    val maxPlayers = roomName.toIntOrNull() ?: 4
                                    gameRoomViewModel.createRoom(RoomRequest(maxPlayers = maxPlayers))
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Error creating room", e)
                                    Toast.makeText(this@MainActivity, "Failed to create room: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        onUpdateRoom = { roomName ->

                        }
                    )
                    "game" -> GameScreen(gameViewModel)
                }
            }
        }
    }

    private suspend fun createNewPlayer(playerApi: PlayerApi): Int {
        val player = playerApi.createPlayer(NewPlayer(name = "Player_${System.currentTimeMillis()}"))
        return player.id
    }
}
