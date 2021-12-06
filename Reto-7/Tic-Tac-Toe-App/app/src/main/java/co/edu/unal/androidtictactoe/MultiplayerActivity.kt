package co.edu.unal.androidtictactoe

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import edu.harding.tictactoe.BoardView
import edu.harding.tictactoe.TicTacToeGame

import io.socket.client.Ack
import io.socket.client.Socket
import org.json.JSONObject



class MultiplayerActivity : AppCompatActivity() {
    private lateinit var mGame: TicTacToeGame
    private lateinit var mMatchInfoTextView: TextView
    private lateinit var mInfoTextView: TextView

    private var mGameOver: Boolean = false

    private var player1WonGames: Int = 0
    private var player2WonGames: Int = 0
    private var tiedGames: Int = 0

    private lateinit var player1CounterTextView: TextView
    private lateinit var player2CounterTextView: TextView
    private lateinit var tieCounterTextView: TextView

    val DIALOG_RESET_ID = 1
    val DIALOG_ABOUT_ID = 2
    val DIALOG_MAIN_MENU = 3
    val DIALOG_ALREADY_RESET_ID = 4

    private lateinit var mBoardView: BoardView
    private lateinit var mTouchListener: mOnTouchListener

    private var waitingPlayer: Boolean = true
    private var pendingPlayerMovement: Boolean = false
    private var wantToReset: Boolean = false
    private var fromMatchList: Boolean = false

    private lateinit var mSocket:Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer)

        mGame = TicTacToeGame()
        mBoardView = findViewById(R.id.board)
        mBoardView.setGame(mGame)

        mTouchListener = mOnTouchListener()
        mBoardView.setOnTouchListener(mTouchListener)

        mInfoTextView = findViewById<View>(R.id.information) as TextView
        mMatchInfoTextView = findViewById<View>(R.id.match) as TextView
        player1CounterTextView = findViewById<View>(R.id.humanWonGames) as TextView
        player2CounterTextView = findViewById<View>(R.id.computerWonGames) as TextView
        tieCounterTextView = findViewById<View>(R.id.tiedGames) as TextView


        player1WonGames = 0
        player2WonGames = 0
        tiedGames = 0

        mSocket = SocketHandler.getSocket()

        mSocket.emit("getMatchNumber", "", Ack {
            mMatchInfoTextView.text = "Match Number: "+it[0].toString()
        })

        val extras = intent.extras
        if (extras != null && extras.getBoolean("Create",false)) {
            //El usuario creó la partida
            waitingPlayer = true
            pendingPlayerMovement = false
            mInfoTextView.setText(R.string.waiting_player)
        }else{
            //El usuario se unió a una partida
            waitingPlayer = false
            pendingPlayerMovement = true
            mInfoTextView.setText(R.string.turn_other_player)
            fromMatchList = true
        }

        displayScores()

        mSocket.on("new-player"){
            waitingPlayer = false
            runOnUiThread { mInfoTextView.setText(R.string.first_human) }
        }

        mSocket.on("receive-play"){ args ->
            val data:Int = (args[0] as JSONObject).get("movement") as Int
            setMove(mGame.COMPUTER_PLAYER, data)
            pendingPlayerMovement = false

            var winner = mGame.checkForWinner()
            if (winner == 0){
                runOnUiThread { mInfoTextView.setText(R.string.turn_human) }

            }else{
                checkWinner(winner)
            }
        }

        mSocket.on("restart-game"){
            startNewGame()
        }

        mSocket.on("quit-player"){
            mGame.clearBoard()
            mBoardView.invalidate()

            player1WonGames = 0
            player2WonGames = 0
            tiedGames = 0

            waitingPlayer = true
            pendingPlayerMovement = false

            runOnUiThread{mInfoTextView.setText(R.string.waiting_player)}
            wantToReset = false
            mGameOver = false

            displayScores()
        }
    }

    private fun startNewGame(){
        mGame.clearBoard()
        mBoardView.invalidate()
        if(pendingPlayerMovement) runOnUiThread { mInfoTextView.setText(R.string.turn_other_player) }
        else runOnUiThread { mInfoTextView.setText(R.string.first_human) }
        wantToReset = false
        mGameOver = false

        displayScores()
    }

    private fun displayScores(){
        runOnUiThread {player1CounterTextView.text = "Player 1: " + player1WonGames }
        runOnUiThread {player2CounterTextView.text = "Player 2: " + player2WonGames }
        runOnUiThread {tieCounterTextView.text = "Tied Games: " + tiedGames }
    }

    private inner class mOnTouchListener : View.OnTouchListener {

        override fun onTouch(v: View, event: MotionEvent):Boolean{
            if(waitingPlayer || pendingPlayerMovement || mGameOver) return false

            // Determine which cell was touched
            val col = (event.x / mBoardView.getBoardCellWidth()).toInt();
            val row = (event.y / mBoardView.getBoardCellHeight()).toInt();
            val pos:Int = row * 3 + col

            if(setMove(mGame.HUMAN_PLAYER, pos)){
                var payload:String = "{movement:${pos}}"
                mSocket.emit("send-play", JSONObject(payload), Ack {
                    var winner = mGame.checkForWinner()
                    pendingPlayerMovement = true
                    if (winner == 0){
                        mInfoTextView.setText(R.string.turn_other_player)
                    }else{
                        checkWinner(winner)
                    }
                })
            }
            return false;
        }

    }

    private fun setMove(player: Char, location: Int): Boolean {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate() // Redraw the board
            return true
        }
        return false;
    }

    fun sendResetRequest(){
        wantToReset = true
        mSocket.emit("restart")
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun checkWinner(winner:Int){
        if (winner == 0) mInfoTextView.setText(R.string.turn_human)
        else if (winner == 1) {
            mInfoTextView.setText(R.string.result_tie)
            tiedGames++
        }else if (winner == 2) {
            mInfoTextView.setText(R.string.result_human_wins)
            player1WonGames++
        }else{
            mInfoTextView.setText(R.string.player2_wins)
            player2WonGames++
        }

        if (winner == 2 || winner == 3) mGameOver = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater: MenuInflater = getMenuInflater();
        inflater.inflate(R.menu.options_multiplayer_menu, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reset -> {
                if(!wantToReset) showDialog(DIALOG_RESET_ID)
                else showDialog(DIALOG_ALREADY_RESET_ID)
                return true
            }
            R.id.quit -> {
                showDialog(DIALOG_MAIN_MENU)
                return true
            }
            R.id.about -> {
                showDialog(DIALOG_ABOUT_ID)
                return true
            }
        }
        return false
    }

    override fun onCreateDialog(id: Int): Dialog? {
        var dialog: Dialog? = null
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        when (id) {

            DIALOG_RESET_ID -> {
                builder.setMessage(R.string.reset_question)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes,
                        DialogInterface.OnClickListener {
                                dialog, id -> sendResetRequest()
                        })
                    .setNegativeButton(R.string.no, null)
                dialog = builder.create()
            }

            DIALOG_ALREADY_RESET_ID -> {
                builder.setMessage(R.string.already_reset)
                    .setCancelable(false)
                    .setNegativeButton(R.string.ok, null)
                dialog = builder.create()
            }

            DIALOG_MAIN_MENU -> {
                builder.setMessage(R.string.quit_question)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes,
                        DialogInterface.OnClickListener {
                                dialog, id -> finish()
                        })
                    .setNegativeButton(R.string.no, null)
                dialog = builder.create()
            }

            DIALOG_ABOUT_ID -> {
                val context = getApplicationContext()
                val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val layout = inflater.inflate(R.layout.about_dialog, null)
                builder.setView(layout)
                builder.setPositiveButton("OK", null)
                dialog = builder.create()
            }
        }
        return dialog
    }

    override fun onStop() {
        super.onStop()

        if(fromMatchList){
            mSocket.emit("quit-match", "", Ack { })
        }else{
            SocketHandler.closeConnection()
        }
    }
}