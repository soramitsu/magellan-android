package jp.co.soramitsu.map.data

import jp.co.soramitsu.map.model.*

interface PlacesRepository {

    fun getCategories(): List<Category>

    fun getPlaceInfo(place: Place): Place

    fun getAllPlaces(
        mapParams: MapParams,
        requestParams: RequestParams
    ): Pair<List<Place>, List<Cluster>>

    fun updatePlaceRating(placeId: String, newRating: Int, comment: String)
    fun deleteReview(userReview: Review, place: Place)
}

data class MapParams(
    val topLeft: GeoPoint,
    val bottomRight: GeoPoint,
    val zoom: Int
)

data class RequestParams(
    val query: String,
    val categoriesIds: List<Long>
)
