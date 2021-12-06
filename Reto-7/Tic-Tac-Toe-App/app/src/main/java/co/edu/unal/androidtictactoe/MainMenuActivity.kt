package co.edu.unal.androidtictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import io.socket.client.Ack


class MainMenuActivity : AppCompatActivity() {

    private lateinit var IA_button: Button
    private lateinit var Create_button: Button
    private lateinit var Join_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        supportActionBar?.hide();

        IA_button = findViewById(R.id.IA) as Button
        Create_button = findViewById(R.id.Create) as Button
        Join_button = findViewById(R.id.Join) as Button


        IA_button.setOnClickListener{
            startActivity(Intent(this@MainMenuActivity, AndroidTicTacToeActivity::class.java))
        }

        Create_button.setOnClickListener{

            // Connect the Android app to the server.
            SocketHandler.setSocket()
            SocketHandler.establishConnection()

            var mSocket = SocketHandler.getSocket()
            var isError = false

            mSocket.emit("create-match", "", Ack {
                isError = true
            })

            if(!isError){
                val Create_Game = Intent(Intent(this@MainMenuActivity, MultiplayerActivity::class.java))
                Create_Game.putExtra("Create",true)
                startActivity(Create_Game)
            }

        }


        Join_button.setOnClickListener{
            startActivity(Intent(this@MainMenuActivity, ListMatchesActivity::class.java))
        }
    }


}