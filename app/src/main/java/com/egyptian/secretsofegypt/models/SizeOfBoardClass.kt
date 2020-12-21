package com.egyptian.secretsofegypt.models

enum class SizeOfBoardClass(val numCards: Int) {
    EASY(8),
    MEDIUM(18),
    HARD(24);

    companion object {
        fun getValue(value: Int) = values().first { it.numCards == value }
    }

    fun getHeight(): Int {
        return numCards / getWidth()
    }

    fun getWidth(): Int {
        return when (this) {
            EASY -> 2
            MEDIUM -> 3
            HARD -> 4
        }
    }

    fun getNumOfPairs(): Int {
        return numCards / 2
    }

}