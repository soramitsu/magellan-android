package jp.co.soramitsu.map.model

import java.util.*

data class Place(
    val name: String,
    val khmerName: String = "",
    val category: Category,
    val position: Position,
    val schedule: Schedule = Schedule(),
    val rating: Float = 0f,
    val phone: String = "",
    val website: String = "",
    val facebook: String = "",
    val address: String = "",
    val userReview: Review? = null,
    val otherReviews: List<Review> = emptyList(),
    val id: String = "",
    val logoUrl: String = "",
    val promoImageUrl: String = "",
    val logo: ByteArray = ByteArray(0),
    val promoImage: ByteArray = ByteArray(0),
) {

    fun localisedName(): String {
        val useKhmerName = Locale.getDefault().language == "km" && khmerName.isNotBlank()
        return if (useKhmerName) khmerName else name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Place

        if (name != other.name) return false
        if (khmerName != other.khmerName) return false
        if (category != other.category) return false
        if (position != other.position) return false
        if (schedule != other.schedule) return false
        if (rating != other.rating) return false
        if (phone != other.phone) return false
        if (website != other.website) return false
        if (facebook != other.facebook) return false
        if (address != other.address) return false
        if (userReview != other.userReview) return false
        if (otherReviews != other.otherReviews) return false
        if (id != other.id) return false
        if (logoUrl != other.logoUrl) return false
        if (promoImageUrl != other.promoImageUrl) return false
        if (!logo.contentEquals(other.logo)) return false
        if (!promoImage.contentEquals(other.promoImage)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + khmerName.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + schedule.hashCode()
        result = 31 * result + rating.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + website.hashCode()
        result = 31 * result + facebook.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + (userReview?.hashCode() ?: 0)
        result = 31 * result + otherReviews.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + logoUrl.hashCode()
        result = 31 * result + promoImageUrl.hashCode()
        result = 31 * result + logo.contentHashCode()
        result = 31 * result + promoImage.contentHashCode()
        return result
    }
}
