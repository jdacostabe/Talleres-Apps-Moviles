package edu.harding.tictactoe

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import co.edu.unal.androidtictactoe.R


class BoardView : View {
    val GRID_WIDTH = 6
    private lateinit var mHumanBitmap: Bitmap
    private lateinit var mComputerBitmap: Bitmap
    private lateinit var mPaint: Paint
    private lateinit var mGame: TicTacToeGame

    constructor(context: Context) : super(context){
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle){
        initialize()
    }

    private fun initialize(){
        mHumanBitmap = BitmapFactory.decodeResource(resources, R.drawable.x)
        mComputerBitmap = BitmapFactory.decodeResource(resources, R.drawable.o)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public fun setGame(game: TicTacToeGame){
        mGame = game
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Determine the width and height of the View
        val boardWidth = width.toFloat()
        val boardHeight = height.toFloat()

        // Make thick, light gray lines
        mPaint.color = Color.LTGRAY
        mPaint.strokeWidth = GRID_WIDTH.toFloat()

        // Draw the two vertical board lines
        val cellWidth = boardWidth / 3
        canvas.drawLine(cellWidth, 0F, cellWidth, boardHeight, mPaint)
        canvas.drawLine(cellWidth * 2, 0F, cellWidth * 2, boardHeight, mPaint)

        // Draw the two horizontal board lines
        val cellHeight = boardHeight / 3
        canvas.drawLine(0F, cellHeight, boardWidth, cellHeight, mPaint)
        canvas.drawLine(0F,cellHeight * 2, boardWidth, cellHeight * 2, mPaint)

        for(i in 0 until mGame.BOARD_SIZE){
            val col:Int = i%3
            val row:Int = i/3

            // Define the boundaries of a destination rectangle for the image
            val left = (cellWidth*col).toInt()
            val top = (cellHeight*row).toInt()
            val right = (cellWidth*(col+1)).toInt()
            val bottom = (cellHeight*(row+1)).toInt()

            if(mGame != null && mGame.getBoardOccupant(i) == mGame.HUMAN_PLAYER){
                canvas.drawBitmap(mHumanBitmap, null, Rect(left, top, right, bottom), null)
            }

            if(mGame != null && mGame.getBoardOccupant(i) == mGame.COMPUTER_PLAYER){
                canvas.drawBitmap(mComputerBitmap, null, Rect(left, top, right, bottom), null)
            }
        }
    }

    fun getBoardCellWidth(): Int {
        return width / 3
    }

    fun getBoardCellHeight(): Int {
        return height / 3
    }


}