package jp.co.soramitsu.map.data

import jp.co.soramitsu.map.model.Category
import jp.co.soramitsu.map.model.Cluster
import jp.co.soramitsu.map.model.Place

internal class DemoPlacesRepository : PlacesRepository {
    override fun getCategories(): List<Category> = emptyList()

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
        }
        return Pair(places, emptyList())
    }
}

/**
 * This object is useless. We only add extension properties to split a list into
 * few separate files. This will help us to avoid opening one monstrous file
 * and dramatically slow down android studio while editing it
 */
internal object Places