package jp.co.soramitsu.map.data

import jp.co.soramitsu.map.model.Category
import jp.co.soramitsu.map.model.Cluster
import jp.co.soramitsu.map.model.GeoPoint
import jp.co.soramitsu.map.model.Place

interface PlacesRepository {

    fun getCategories(): List<Category>

    fun getPlaceInfo(place: Place): Place

    fun getAllPlaces(
        mapParams: MapParams,
        requestParams: RequestParams
    ): Pair<List<Place>, List<Cluster>>
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
