package edu.harding.tictactoe

import java.util.*

public class TicTacToeGame {

    // The computer's difficulty levels
    enum class DifficultyLevel {
        Easy, Harder, Expert
    }

    val BOARD_SIZE: Int = 9
    val HUMAN_PLAYER = 'X'
    val COMPUTER_PLAYER = 'O'
    val OPEN_SPOT = ' '

    // Current difficulty level
    private var mDifficultyLevel = DifficultyLevel.Expert

    private val mBoard = charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ')

    private val mRand: Random

    // Check for a winner.  Return
    //  0 if no winner or tie yet
    //  1 if it's a tie
    //  2 if X won
    //  3 if O won
    public fun checkForWinner(): Int {

        // Check horizontal wins
        var i = 0
        while (i <= 6) {
            if (mBoard[i] == HUMAN_PLAYER && mBoard[i + 1] == HUMAN_PLAYER && mBoard[i + 2] == HUMAN_PLAYER) return 2
            if (mBoard[i] == COMPUTER_PLAYER && mBoard[i + 1] == COMPUTER_PLAYER && mBoard[i + 2] == COMPUTER_PLAYER) return 3
            i += 3
        }

        // Check vertical wins
        for (i in 0..2) {
            if (mBoard[i] == HUMAN_PLAYER && mBoard[i + 3] == HUMAN_PLAYER && mBoard[i + 6] == HUMAN_PLAYER) return 2
            if (mBoard[i] == COMPUTER_PLAYER && mBoard[i + 3] == COMPUTER_PLAYER && mBoard[i + 6] == COMPUTER_PLAYER) return 3
        }

        // Check for diagonal wins
        if (mBoard[0] == HUMAN_PLAYER && mBoard[4] == HUMAN_PLAYER && mBoard[8] == HUMAN_PLAYER ||
            mBoard[2] == HUMAN_PLAYER && mBoard[4] == HUMAN_PLAYER && mBoard[6] == HUMAN_PLAYER
        ) return 2
        if (mBoard[0] == COMPUTER_PLAYER && mBoard[4] == COMPUTER_PLAYER && mBoard[8] == COMPUTER_PLAYER ||
            mBoard[2] == COMPUTER_PLAYER && mBoard[4] == COMPUTER_PLAYER && mBoard[6] == COMPUTER_PLAYER
        ) return 3

        // Check for tie
        for (i in 0 until BOARD_SIZE) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                return 0
            }
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1
    }


    // Return the best move for the computer to make. You must call setMove()
    // to actually make the computer move to that location.
    // @return The best move for the computer to make (0-8).
    fun getComputerMove(): Int {
        var move = -1
        if (mDifficultyLevel === DifficultyLevel.Easy)
            move = getRandomMove()
        else if (mDifficultyLevel === DifficultyLevel.Harder) {
            move = getWinningMove()
            if (move == -1) move = getRandomMove()
        } else if (mDifficultyLevel === DifficultyLevel.Expert) {
            // Try to win, but if that's not possible, block.
            // If that's not possible, move anywhere.
            move = getWinningMove()
            if (move == -1) move = getBlockingMove()
            if (move == -1) move = getRandomMove()
        }
        return move
    }

    fun getRandomMove(): Int{
        var move: Int

        do {
            move = mRand.nextInt(BOARD_SIZE)
        } while (mBoard[move] != OPEN_SPOT)

        return move
    }

    fun getBlockingMove(): Int{
        for (i in 0 until BOARD_SIZE) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = HUMAN_PLAYER
                if (checkForWinner() == 2) {
                    mBoard[i] = OPEN_SPOT
                    return i
                }
                mBoard[i] = OPEN_SPOT
            }
        }
        return -1
    }

    fun getWinningMove(): Int{
        for (i in 0 until BOARD_SIZE) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = COMPUTER_PLAYER
                if (checkForWinner() == 3){
                    mBoard[i] = OPEN_SPOT
                    return i
                }
                mBoard[i] = OPEN_SPOT
            }
        }
        return -1
    }

    // Clear the board of all X's and O's by setting all spots to OPEN_SPOT
    public fun clearBoard(){
        for (i in 0 until BOARD_SIZE) mBoard[i] = OPEN_SPOT
    }

    // Set the given player at the given location on the game board.
    // The location must be available, or the board will not be changed.
    // @param player - The HUMAN_PLAYER or COMPUTER_PLAYER
    // @param location - The location (0-8) to place the move
    public fun setMove(player:Char, location:Int): Boolean{
        if(mBoard[location] == OPEN_SPOT){
            mBoard[location] = player
            return true
        }
        return false
    }

    public fun getDifficultyLevel(): DifficultyLevel {
        return mDifficultyLevel
    }

    public fun setDifficultyLevel(difficultyLevel: DifficultyLevel) {
        mDifficultyLevel = difficultyLevel
    }

    public fun getBoardOccupant(location:Int):Char{
        return mBoard[location]
    }

    init {
        mRand = Random()
    }
}