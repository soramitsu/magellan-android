package jp.co.soramitsu.feature.maps.impl.data

import jp.co.soramitsu.feature.maps.impl.model.Place

interface PlacesRepository {
    fun getAllPlaces(): List<Place>
}