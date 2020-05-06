package jp.co.soramitsu.map.model

internal data class Review(
    val author: Author,
    val rating: Float,
    val date: Long,
    val text: String
)