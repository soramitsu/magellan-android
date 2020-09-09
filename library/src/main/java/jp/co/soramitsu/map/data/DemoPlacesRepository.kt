package jp.co.soramitsu.map.data

import jp.co.soramitsu.map.model.*
import java.util.*

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
            val schedule = if (index % 2 == 0) {
                Schedule(open24 = true)
            } else {
                Schedule(
                    workingDays = listOf(
                        WorkDay(
                            weekDay = Calendar.MONDAY,
                            from = Time(5, 0),
                            to = Time(20, 0),
                            launchTimeFrom = Time(7, 0),
                            launchTimeTo = Time(17, 0)
                        ),
                        WorkDay(
                            weekDay = Calendar.TUESDAY,
                            from = Time(5, 0),
                            to = Time(20, 0),
                            launchTimeFrom = Time(7, 0),
                            launchTimeTo = Time(17, 0)
                        ),
                        WorkDay(
                            weekDay = Calendar.WEDNESDAY,
                            from = Time(5, 0),
                            to = Time(20, 0),
                            launchTimeFrom = Time(7, 0),
                            launchTimeTo = Time(17, 0)
                        ),
                        WorkDay(
                            weekDay = Calendar.THURSDAY,
                            from = Time(5, 0),
                            to = Time(20, 0),
                            launchTimeFrom = Time(7, 0),
                            launchTimeTo = Time(17, 0)
                        ),
                        WorkDay(
                            weekDay = Calendar.FRIDAY,
                            from = Time(5, 0),
                            to = Time(20, 0)
                        )
                    )
                )
            }
            place.copy(
                id = index.toString(),
                schedule = schedule
            )
        }.subList(5, 30)
        val clusters = listOf(
            Cluster(GeoPoint(places[0].position.latitude, places[0].position.longitude), 3),
            Cluster(GeoPoint(places[1].position.latitude, places[1].position.longitude), 30),
            Cluster(GeoPoint(places[2].position.latitude, places[2].position.longitude), 99)
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