package jp.co.soramitsu.map

import jp.co.soramitsu.map.data.DemoPlacesRepository
import jp.co.soramitsu.map.data.PlacesRepository

object SoramitsuMapLibraryConfig {
    var repository: PlacesRepository = DemoPlacesRepository()
}