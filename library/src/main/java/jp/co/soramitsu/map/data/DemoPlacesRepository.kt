package jp.co.soramitsu.map.data

import jp.co.soramitsu.map.model.Category
import jp.co.soramitsu.map.model.Cluster
import jp.co.soramitsu.map.model.GeoPoint
import jp.co.soramitsu.map.model.Place

internal class DemoPlacesRepository : PlacesRepository {
    override fun getCategories(): List<Category> = listOf(
        Category.BANK,
        Category.FOOD,
        Category.SERVICES,
        Category.SUPERMARKETS,
        Category.PHARMACY,
        Category.ENTERTAINMENT,
        Category.EDUCATION,
        Category.OTHER
    )

    override fun getPlaceInfo(place: Place): Place {
        val allPlaces = Places.merchants + Places.banks + Places.agents
        val foundPlace = allPlaces.find { it.id == place.id }
        return foundPlace ?: place
    }

    override fun getAllPlaces(
        mapParams: MapParams,
        requestParams: RequestParams
    ): Pair<List<Place>, List<Cluster>> {
        val places = (Places.merchants + Places.banks + Places.agents).mapIndexed { index, place ->
            place.copy(id = index.toString())
        }.subList(5, 30)
        val clusters = listOf(
            Cluster(GeoPoint(places[0].position.latitude, places[0].position.longitude), 3),
            Cluster(GeoPoint(places[0].position.latitude, places[0].position.longitude), 30),
            Cluster(GeoPoint(places[0].position.latitude, places[0].position.longitude), 99)
        )
        return Pair(places, clusters)
    }
}

/**
 * This object is useless. We only add extension properties to split a list into
 * few separate files. This will help us to avoid opening one monstrous file
 * and dramatically slow down android studio while editing it
 */
internal object Places