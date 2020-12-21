package com.egyptian.secretsofegypt.models
import com.egyptian.secretsofegypt.models.SizeOfBoardClass
import com.egyptian.secretsofegypt.utils.DEFAULT_ICONS

class MemoryGame(private val sizeOfBoardClass: SizeOfBoardClass, customImages: List<String>?) {
    val cards: List<MemoCards>
    var numPairsFound: Int = 0

    private var numCardsFlip = 0
    private var singleSelectedCardsIndex: Int? = null

    init {
        if (customImages == null) {
            val imageChoose = DEFAULT_ICONS.shuffled().take(sizeOfBoardClass.getNumOfPairs())
            val imagesRandom = (imageChoose + imageChoose).shuffled()
            cards = imagesRandom.map { MemoCards(it) }
        } else {
            val randomizedImages = (customImages + customImages).shuffled()
            cards = randomizedImages.map { MemoCards(it.hashCode(), it) }
        }

    }

    fun fCard(position: Int): Boolean {
        numCardsFlip++
        val card = cards[position]
        var foundMatch = false
        if (singleSelectedCardsIndex == null) {
            restoreCards()
            singleSelectedCardsIndex = position
        } else {
            foundMatch = checkForMatch(singleSelectedCardsIndex!!, position)
            singleSelectedCardsIndex = null
        }
        card.isFaceUp = !card.isFaceUp
        return foundMatch
    }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        return if (cards[position1].identify == cards[position2].identify) {
            cards[position1].isMatched = true
            cards[position2].isMatched = true
            numPairsFound++
            true
        } else false
    }

    private fun restoreCards() {
        for (card in cards) {
            if (!card.isMatched) {
                card.isFaceUp = false
            }
        }
    }

    fun haveWonGame(): Boolean {
        return numPairsFound == sizeOfBoardClass.getNumOfPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
        return numCardsFlip / 2
    }
}
