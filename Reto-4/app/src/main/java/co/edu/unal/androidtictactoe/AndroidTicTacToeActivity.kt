package co.edu.unal.androidtictactoe

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import edu.harding.tictactoe.TicTacToeGame
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import android.content.DialogInterface
import android.view.LayoutInflater








class AndroidTicTacToeActivity : AppCompatActivity() {

    private lateinit var mGame: TicTacToeGame
    private var mGameOver: Boolean = false
    private lateinit var mBoardButtons: Array<Button>
    private lateinit var mInfoTextView: TextView
    private var humanGoesFirst:Boolean = false

    private var humanWonGames:Int = 0
    private var computerWonGames:Int = 0
    private var tiedGames:Int = 0

    private lateinit var humanCounterTextView: TextView
    private lateinit var compuerCounterTextView: TextView
    private lateinit var tieCounterTextView: TextView

    val DIALOG_DIFFICULTY_ID = 0
    val DIALOG_QUIT_ID = 1
    val DIALOG_ABOUT_ID = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mGame = TicTacToeGame()
        mBoardButtons = Array(mGame.BOARD_SIZE) { i -> getButton(i) }
        mInfoTextView = findViewById<View>(R.id.information) as TextView
        humanCounterTextView = findViewById<View>(R.id.humanWonGames) as TextView
        compuerCounterTextView = findViewById<View>(R.id.computerWonGames) as TextView
        tieCounterTextView = findViewById<View>(R.id.tiedGames) as TextView

        startNewGame()
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
            R.id.quit -> {
                showDialog(DIALOG_QUIT_ID)
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
                    })
                dialog = builder.create()
            }

            DIALOG_QUIT_ID -> {
                // Create the quit confirmation dialog
                // Create the quit confirmation dialog
                builder.setMessage(R.string.quit_question)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes,
                        DialogInterface.OnClickListener { dialog, id -> finish() })
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

    private fun getButton(id:Int):Button{
        when (id) {
            0 -> return findViewById<View>(R.id.one) as Button
            1 -> return findViewById<View>(R.id.two) as Button
            2 -> return findViewById<View>(R.id.three) as Button
            3 -> return findViewById<View>(R.id.four) as Button
            4 -> return findViewById<View>(R.id.five) as Button
            5 -> return findViewById<View>(R.id.six) as Button
            6 -> return findViewById<View>(R.id.seven) as Button
            7 -> return findViewById<View>(R.id.eight) as Button
        }
        return findViewById<View>(R.id.nine) as Button
    }

    private fun startNewGame(){
        mGame.clearBoard()
        mGameOver = false

        for (i in mBoardButtons.indices) {
            mBoardButtons[i].setText("")
            mBoardButtons[i].setEnabled(true)
            mBoardButtons[i].setOnClickListener(ButtonClickListener(i))
        }

        humanCounterTextView.setText("Human " + humanWonGames)
        compuerCounterTextView.setText("Android " + computerWonGames)
        tieCounterTextView.setText("Tied: " + tiedGames)

        humanGoesFirst = !humanGoesFirst
        if(humanGoesFirst) mInfoTextView.setText(R.string.first_human)
        else {
            mInfoTextView.setText(R.string.first_computer)
            val move: Int = mGame.getComputerMove()
            mGameOver = true

            Handler(Looper.getMainLooper()).postDelayed({
                setMove(mGame.COMPUTER_PLAYER, move)
                mGameOver = false
            }, 2000)
        }
    }

    private inner class ButtonClickListener(var location: Int) : View.OnClickListener {
        override fun onClick(view: View) {
            if (mBoardButtons[location].isEnabled() && !mGameOver) {
                setMove(mGame.HUMAN_PLAYER, location)

                var winner: Int = mGame.checkForWinner()
                mGameOver = true

                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer)
                    val move: Int = mGame.getComputerMove()

                    Handler(Looper.getMainLooper()).postDelayed({
                        setMove(mGame.COMPUTER_PLAYER, move)
                        winner = mGame.checkForWinner()
                        mGameOver = false
                        checkWinner(winner)
                    }, 1000)
                }else{
                    checkWinner(winner)
                }
            }
        }
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

    private fun setMove(player: Char, location: Int) {
        mGame.setMove(player, location)
        mBoardButtons[location].setEnabled(false)
        mBoardButtons[location].setText(player.toString())
        if (player == mGame.HUMAN_PLAYER) mBoardButtons[location].setTextColor(Color.rgb(0,200,0))
        else mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0))
    }
}