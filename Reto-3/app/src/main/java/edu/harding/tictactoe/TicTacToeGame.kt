package edu.harding.tictactoe

import java.util.*

public class TicTacToeGame {
    private val mBoard = charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ')

    public val BOARD_SIZE = 9
    public val HUMAN_PLAYER = 'X'
    public val COMPUTER_PLAYER = 'O'
    public val OPEN_SPOT = ' '

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
    public fun getComputerMove(): Int{

        // First see if there's a move Computer can make to win
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

        // See if there's a move O can make to block Human from winning
        for (i in 0 until BOARD_SIZE) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = HUMAN_PLAYER
                if (checkForWinner() == 2) {
                    mBoard[i] = OPEN_SPOT // o COMPUTER_PLAYER
                    return i
                }
                mBoard[i] = OPEN_SPOT
            }
        }

        // Generate random move
        var move: Int

        do {
            move = mRand.nextInt(BOARD_SIZE)
        } while (mBoard[move] != OPEN_SPOT)

        return move
    }


    // Clear the board of all X's and O's by setting all spots to OPEN_SPOT
    public fun clearBoard(){
        for (i in 0 until BOARD_SIZE) mBoard[i] = OPEN_SPOT
    }

    // Set the given player at the given location on the game board.
    // The location must be available, or the board will not be changed.
    // @param player - The HUMAN_PLAYER or COMPUTER_PLAYER
    // @param location - The location (0-8) to place the move
    public fun setMove(player:Char, location:Int){
        if(mBoard[location] == OPEN_SPOT) mBoard[location] = player
    }

    init {
        mRand = Random()
    }
}