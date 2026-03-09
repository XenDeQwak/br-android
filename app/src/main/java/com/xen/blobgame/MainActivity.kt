package com.xen.blobgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import com.xen.blobgame.data.remote.GameRoomApi
import com.xen.blobgame.data.remote.GameStateSTOMP
import com.xen.blobgame.data.remote.PlayerApi
import com.xen.blobgame.data.repository.GameRoomRepository
import com.xen.blobgame.data.repository.PlayerGameStateRepository
import com.xen.blobgame.data.repository.PlayerRepository
import com.xen.blobgame.ui.composables.GameScreen
import com.xen.blobgame.ui.composables.LoginScreen
import com.xen.blobgame.ui.composables.MenuScreen
import com.xen.blobgame.ui.theme.BlobGameTheme
import com.xen.blobgame.ui.viewmodel.GameRoomViewModel
import com.xen.blobgame.ui.viewmodel.GameStateViewModel
import com.xen.blobgame.ui.viewmodel.PlayerViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private var currentScreen = mutableStateOf("login")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Player setup
        val playerApi = retrofit.create(PlayerApi::class.java)
        val playerRepository = PlayerRepository(playerApi)
        val playerFactory = PlayerViewModel.Factory(playerRepository)
        val playerViewModel = ViewModelProvider(this, playerFactory)[PlayerViewModel::class.java]

        // Room setup
        val gameRoomApi = retrofit.create(GameRoomApi::class.java)
        val gameRoomRepository = GameRoomRepository(gameRoomApi)
        val gameRoomFactory = GameRoomViewModel.Factory(gameRoomRepository)
        val gameRoomViewModel = ViewModelProvider(this, gameRoomFactory)[GameRoomViewModel::class.java]

        // Game state (WebSocket) setup
        val gameStateSTOMP = GameStateSTOMP()
        val gameStateRepository = PlayerGameStateRepository(gameStateSTOMP)
        val gameStateFactory = GameStateViewModel.Factory(gameStateRepository)
        val gameStateViewModel = ViewModelProvider(this, gameStateFactory)[GameStateViewModel::class.java]

        setContent {
            BlobGameTheme {
                when (currentScreen.value) {
                    "login" -> LoginScreen(
                        playerViewModel = playerViewModel,
                        onPlayerCreated = {
                            currentScreen.value = "menu"
                        }
                    )
                    "menu" -> MenuScreen(
                        gameRoomViewModel = gameRoomViewModel,
                        playerViewModel = playerViewModel,
                        onRoomJoined = {
                            currentScreen.value = "game"
                        },
                        onRoomCreated = {
                            currentScreen.value = "game"
                        }
                    )
                    "game" -> GameScreen(
                        gameStateViewModel = gameStateViewModel,
                        playerViewModel = playerViewModel,
                        gameRoomViewModel = gameRoomViewModel,
                        onGameDone = {
                            currentScreen.value = "menu"
                        }
                    )
                }
            }
        }
    }
}