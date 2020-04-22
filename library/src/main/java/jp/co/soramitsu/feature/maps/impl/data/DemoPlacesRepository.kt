package jp.co.soramitsu.feature.maps.impl.data

import jp.co.soramitsu.feature.maps.impl.model.Place

class DemoPlacesRepository : PlacesRepository {

    override fun getAllPlaces(): List<Place> = Places.merchants + Places.banks + Places.agents
}

/**
 * This object is useless. We only add extension properties to split a list into
 * few separate files. This will help us to avoid opening one monstrous file
 * and dramatically slow down android studio while editing it
 */
object Places