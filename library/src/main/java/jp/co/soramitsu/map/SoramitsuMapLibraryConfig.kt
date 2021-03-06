package jp.co.soramitsu.map

import jp.co.soramitsu.map.data.DemoPlacesRepository
import jp.co.soramitsu.map.data.PlacesRepository
import jp.co.soramitsu.map.model.Position

object SoramitsuMapLibraryConfig {
    var defaultZoom = 10f
    var defaultPosition: Position = Position(11.541789, 104.913587)
    var repository: PlacesRepository = DemoPlacesRepository()

    var enableDetailedSchedule: Boolean = BuildConfig.DEBUG
    var enablePlaceProposal: Boolean = BuildConfig.DEBUG
    var enablePlaceReviews: Boolean = BuildConfig.DEBUG

    var enableMultiplePlacePhotos: Boolean = false

    var logger: Logger = object : Logger {}
}
