package co.edu.unal.androidtictactoe

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import edu.harding.tictactoe.TicTacToeGame
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import android.view.View.OnTouchListener

import android.content.DialogInterface
import android.media.MediaPlayer
import android.view.*
import edu.harding.tictactoe.BoardView
import android.content.SharedPreferences


class AndroidTicTacToeActivity : AppCompatActivity() {

    private lateinit var mGame: TicTacToeGame
    private lateinit var mInfoTextView: TextView

    private var mGameOver: Boolean = false
    private var humanGoesFirst: Boolean = false

    private var humanWonGames: Int = 0
    private var computerWonGames: Int = 0
    private var tiedGames: Int = 0

    private lateinit var humanCounterTextView: TextView
    private lateinit var compuerCounterTextView: TextView
    private lateinit var tieCounterTextView: TextView

    val DIALOG_DIFFICULTY_ID = 0
    val DIALOG_RESET_ID = 1
    val DIALOG_ABOUT_ID = 2
    val DIALOG_MAIN_MENU = 3

    private lateinit var mBoardView: BoardView
    private lateinit var mTouchListener: mOnTouchListener

    private lateinit var mHumanMediaPlayer: MediaPlayer
    private lateinit var mComputerMediaPlayer: MediaPlayer

    private lateinit var mPrefs: SharedPreferences
    private var pendingComputerMovement: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mGame = TicTacToeGame()
        mBoardView = findViewById(R.id.board)
        mBoardView.setGame(mGame)

        mTouchListener = mOnTouchListener()
        mBoardView.setOnTouchListener(mTouchListener);

        mInfoTextView = findViewById<View>(R.id.information) as TextView
        humanCounterTextView = findViewById<View>(R.id.humanWonGames) as TextView
        compuerCounterTextView = findViewById<View>(R.id.computerWonGames) as TextView
        tieCounterTextView = findViewById<View>(R.id.tiedGames) as TextView

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE)
        humanWonGames = mPrefs.getInt("humanWonGames", 0)
        computerWonGames = mPrefs.getInt("computerWonGames", 0)
        tiedGames = mPrefs.getInt("tiedGames", 0)
        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[mPrefs.getInt("difficulty", 2)])

        if(savedInstanceState == null) startNewGame()
    }

    private fun startNewGame(){
        mGame.clearBoard()
        mBoardView.invalidate()
        mGameOver = false

        displayScores()

        humanGoesFirst = !humanGoesFirst

        if(humanGoesFirst)
            mInfoTextView.setText(R.string.first_human)
        else {
            mInfoTextView.setText(R.string.first_computer)
            val move: Int = mGame.getComputerMove()
            pendingComputerMovement = true
            mGameOver = true

            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    setMove(mGame.COMPUTER_PLAYER, move)
                }catch (e: Exception){ }
                mGameOver = false
                pendingComputerMovement = false
            }, 1000)
        }
    }

    private fun displayScores(){
        humanCounterTextView.text = "Human: " + humanWonGames
        compuerCounterTextView.text = "Android: " + computerWonGames
        tieCounterTextView.text = "Tied: " + tiedGames
    }

    private inner class mOnTouchListener : OnTouchListener {

        override fun onTouch(v: View, event: MotionEvent):Boolean{
            // Determine which cell was touched
            val col = (event.x / mBoardView.getBoardCellWidth()).toInt();
            val row = (event.y / mBoardView.getBoardCellHeight()).toInt();
            val pos:Int = row * 3 + col

            if(!mGameOver && setMove(mGame.HUMAN_PLAYER, pos)){

                // If no winner yet, let the computer make a move
                var winner = mGame.checkForWinner()
                mGameOver = true

                if (winner == 0) {
                    pendingComputerMovement = true
                    mInfoTextView.setText(R.string.turn_computer)
                    val move: Int = mGame.getComputerMove()

                    Handler(Looper.getMainLooper()).postDelayed({
                        try {
                            setMove(mGame.COMPUTER_PLAYER, move)
                        }catch (e: Exception){ }

                        mGameOver = false
                        checkWinner(mGame.checkForWinner())
                        pendingComputerMovement = false
                    }, 1000)
                }else{
                    checkWinner(winner)
                }
            }
            // So we aren't notified of continued events when finger is moved
            return false;
        }

    }

    private fun setMove(player: Char, location: Int): Boolean {
        if (mGame.setMove(player, location)) {
            if(player == mGame.HUMAN_PLAYER) mHumanMediaPlayer.start()
            else if (player == mGame.COMPUTER_PLAYER) mComputerMediaPlayer.start()
            mBoardView.invalidate() // Redraw the board
            return true
        }
        return false;
    }

    private fun checkWinner(winner:Int){
        if (winner == 0) mInfoTextView.setText(R.string.turn_human)
        else if (winner == 1) {
            mInfoTextView.setText(R.string.result_tie)
            tiedGames++
        }else if (winner == 2) {
            mInfoTextView.setText(R.string.result_human_wins)
            humanWonGames++
        }else{
            mInfoTextView.setText(R.string.result_computer_wins)
            computerWonGames++
        }

        if (winner == 2 || winner == 3) mGameOver = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater: MenuInflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_game -> {
                startNewGame()
                return true
            }
            R.id.ai_difficulty -> {
                showDialog(DIALOG_DIFFICULTY_ID)
                return true
            }
            R.id.reset -> {
                showDialog(DIALOG_RESET_ID)
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

            DIALOG_DIFFICULTY_ID -> {
                builder.setTitle(R.string.difficulty_choose)

                val levels = arrayOf<CharSequence>(
                    resources.getString(R.string.difficulty_easy),
                    resources.getString(R.string.difficulty_harder),
                    resources.getString(R.string.difficulty_expert)
                )

                val selected = mGame.getDifficultyLevel().ordinal

                // selected is the radio button that should be selected.
                builder.setSingleChoiceItems(levels, selected,
                    DialogInterface.OnClickListener { dialog, item ->
                        dialog.dismiss() // Close dialog
                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[item])
                        // Display the selected difficulty level
                        Toast.makeText(applicationContext, levels[item], Toast.LENGTH_SHORT).show()
                        startNewGame()
                    })
                dialog = builder.create()
            }

            DIALOG_RESET_ID -> {
                // Reset Scores
                humanWonGames = 0
                computerWonGames = 0
                tiedGames = 0
                displayScores()
            }

            DIALOG_MAIN_MENU -> {
                // Create the quit confirmation dialog
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

    override fun onResume() {
        super.onResume()

        mHumanMediaPlayer = MediaPlayer.create(applicationContext, R.raw.human)
        mComputerMediaPlayer = MediaPlayer.create(applicationContext, R.raw.android)
    }

    override fun onPause() {
        super.onPause()

        mHumanMediaPlayer.release()
        mComputerMediaPlayer.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState);

        outState.putCharArray("board", mGame.getBoardState())
        outState.putBoolean("mGameOver", mGameOver)
        outState.putCharSequence("info", mInfoTextView.text)
        outState.putBoolean("humanGoesFirst", humanGoesFirst)
        outState.putBoolean("pendingComputerMovement", pendingComputerMovement)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        mGame.setBoardState(savedInstanceState.getCharArray("board"))
        mGameOver = savedInstanceState.getBoolean("mGameOver")
        mInfoTextView.text = savedInstanceState.getCharSequence("info")
        humanGoesFirst = savedInstanceState.getBoolean("humanGoesFirst")
        displayScores()

        if(savedInstanceState.getBoolean("pendingComputerMovement")){
            mGame.setMove(mGame.COMPUTER_PLAYER, mGame.getComputerMove())
            mBoardView.invalidate()
            mGameOver = false
            checkWinner(mGame.checkForWinner())
        }
    }

    override fun onStop() {
        super.onStop()

        val ed = mPrefs.edit()
        ed.putInt("humanWonGames", humanWonGames)
        ed.putInt("computerWonGames", computerWonGames)
        ed.putInt("tiedGames", tiedGames)
        ed.putInt("difficulty", mGame.getDifficultyLevel().ordinal)
        ed.commit()
    }

}