package com.egyptian.secretsofegypt.models

data class MemoCards(
    val identify: Int,
    var imageUrl: String? = null,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
) {
}