package com.xen.blobgame
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import com.xen.blobgame.data.remote.PlayerApi
import com.xen.blobgame.data.repository.PlayerRepository
import com.xen.blobgame.ui.composables.LoginScreen
import com.xen.blobgame.ui.theme.BlobGameTheme
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

        val playerApi = retrofit.create(PlayerApi::class.java)
        val playerRepository = PlayerRepository(playerApi)
        val playerFactory = PlayerViewModel.Factory(playerRepository)
        val playerViewModel = ViewModelProvider(this, playerFactory)[PlayerViewModel::class.java]

        setContent {
            BlobGameTheme {
                when (currentScreen.value) {
                    "login" -> LoginScreen(
                        playerViewModel = playerViewModel,
                        onPlayerCreated = {
                            currentScreen.value = "room"
                        }
                    )
                }
            }
        }
    }
}
