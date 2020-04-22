package jp.co.soramitsu.feature.maps.impl.model

data class Review(
    val author: Author,
    val rating: Float,
    val date: Long,
    val text: String
)