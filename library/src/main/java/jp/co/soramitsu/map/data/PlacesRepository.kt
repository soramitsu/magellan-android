package jp.co.soramitsu.map.data

import jp.co.soramitsu.map.model.Place

internal interface PlacesRepository {
    fun getAllPlaces(): List<Place>
}