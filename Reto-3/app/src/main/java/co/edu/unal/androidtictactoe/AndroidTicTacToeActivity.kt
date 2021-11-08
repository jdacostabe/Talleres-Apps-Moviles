package co.edu.unal.androidtictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import edu.harding.tictactoe.TicTacToeGame
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView

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
        menu.add("New Game")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startNewGame()
        return true
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