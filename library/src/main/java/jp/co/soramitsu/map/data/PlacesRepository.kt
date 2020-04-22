package jp.co.soramitsu.map.data

import jp.co.soramitsu.map.model.Place

interface PlacesRepository {
    fun getAllPlaces(): List<Place>
}