package com.egyptian.secretsofegypt

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.jinatonic.confetti.CommonConfetti
import com.google.android.material.snackbar.Snackbar
import com.egyptian.secretsofegypt.models.SizeOfBoardClass
import com.egyptian.secretsofegypt.models.MemoryGame

class MainActivity : AppCompatActivity() {
    private lateinit var board: RecyclerView
    private lateinit var numOfMoves: TextView
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var numOfPairs: TextView
    private var gameName: String? = null
    private var cGameImg: List<String>? = null
    private lateinit var adapter: MemoryBoardAdapter
    private lateinit var memoGame: MemoryGame
    private var sizeOfBoardClass: SizeOfBoardClass = SizeOfBoardClass.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        board = findViewById(R.id.rvBoard)
        numOfMoves = findViewById(R.id.tvMove)
        numOfPairs = findViewById(R.id.tvPairs)
        rootLayout = findViewById(R.id.tvLayout)
        setB()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val CREATE_REQUEST_CODE = 328
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> {
                if (memoGame.getNumMoves() > 0 && !memoGame.haveWonGame()) {
                    showAlertDialog("Quit your current game?", null) {
                        setB()
                    }
                } else {
                    setB()
                }
                return true
            }
            R.id.newSize -> {
                showNewAlert()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    @SuppressLint("SetTextI18n")
    private fun setB() {
        supportActionBar?.title = gameName ?: getString(R.string.app_name)
        when (sizeOfBoardClass) {
            SizeOfBoardClass.EASY -> {
                numOfMoves.text = "EASY: 4 x 2"
                numOfPairs.text = "Pairs: 0/${sizeOfBoardClass.getNumOfPairs()}"
            }
            SizeOfBoardClass.MEDIUM -> {
                numOfMoves.text = "MEDIUM: 6 x 3"
                numOfPairs.text = "Pairs: 0/${sizeOfBoardClass.getNumOfPairs()}"
            }
            SizeOfBoardClass.HARD -> {
                numOfMoves.text = "HARD: 6 x 6"
                numOfPairs.text = "Pairs: 0/${sizeOfBoardClass.getNumOfPairs()}"
            }

        }
        numOfPairs.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))
        memoGame = MemoryGame(sizeOfBoardClass, cGameImg)
        adapter = MemoryBoardAdapter(
            this,
            sizeOfBoardClass,
            memoGame.cards,
            object : MemoryBoardAdapter.CardClickListener {
                override fun onCardClicked(position: Int) {
                    updateGame(position)
                }
            })

        board.adapter = adapter
        board.setHasFixedSize(true) //set fixed size
        board.layoutManager = GridLayoutManager(this, sizeOfBoardClass.getWidth())
    }

    @SuppressLint("SetTextI18n")
    private fun updateGame(position: Int) {
        if (memoGame.haveWonGame()) {
            Snackbar.make(rootLayout, "You've already won!", Snackbar.LENGTH_LONG).show()
            return
        }
        if (memoGame.isCardFaceUp(position)) {
            Snackbar.make(rootLayout, "Invalid Move", Snackbar.LENGTH_SHORT).show()
            return
        }

        if (memoGame.fCard(position)) {

            val color = ArgbEvaluator().evaluate(
                memoGame.numPairsFound.toFloat() / sizeOfBoardClass.getNumOfPairs(),
                ContextCompat.getColor(this, R.color.color_progress_none),
                ContextCompat.getColor(this, R.color.color_progress_full)
            ) as Int
            numOfPairs.setTextColor(color)

            numOfPairs.text = "Pairs: ${memoGame.numPairsFound}/${sizeOfBoardClass.getNumOfPairs()}"

            if (memoGame.haveWonGame()) {
                Snackbar.make(
                    rootLayout,
                    "Congratulations!! You've won the game.",
                    Snackbar.LENGTH_LONG
                ).show()
                CommonConfetti.rainingConfetti(
                    rootLayout,
                    intArrayOf(Color.YELLOW, Color.GREEN, Color.BLUE, Color.RED, Color.MAGENTA)
                ).oneShot()
            }
        }

        numOfMoves.text = "Moves: ${memoGame.getNumMoves()}"
        adapter.notifyDataSetChanged() //notify adapter
    }

    @SuppressLint("InflateParams")
    private fun showNewAlert() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)

        when (sizeOfBoardClass) {
            SizeOfBoardClass.EASY -> radioGroupSize.check(R.id.rbEasy)
            SizeOfBoardClass.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            SizeOfBoardClass.HARD -> radioGroupSize.check(R.id.rbHard)
        }

        showAlertDialog("Choose New Size", boardSizeView) {
            sizeOfBoardClass = when (radioGroupSize.checkedRadioButtonId) {
                R.id.rbEasy -> SizeOfBoardClass.EASY
                R.id.rbMedium -> SizeOfBoardClass.MEDIUM
                else -> SizeOfBoardClass.HARD
            }
            gameName = null
            cGameImg = null
            setB()
        }
    }

    private fun showAlertDialog(
        title: String,
        view: View?,
        positiveButtonClickListener: View.OnClickListener
    ) {

        AlertDialog.Builder(this, R.style.MyDialogTheme)
            .setTitle(title).setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Okay") { _, _ ->
                positiveButtonClickListener.onClick(null)
            }.show()
    }

}