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
                            lunchTimeFrom = Time(7, 0),
                            lunchTimeTo = Time(17, 0)
                        ),
                        WorkDay(
                            weekDay = Calendar.TUESDAY,
                            from = Time(5, 0),
                            to = Time(20, 0),
                            lunchTimeFrom = Time(7, 0),
                            lunchTimeTo = Time(17, 0)
                        ),
                        WorkDay(
                            weekDay = Calendar.WEDNESDAY,
                            from = Time(5, 0),
                            to = Time(20, 0),
                            lunchTimeFrom = Time(7, 0),
                            lunchTimeTo = Time(17, 0)
                        ),
                        WorkDay(
                            weekDay = Calendar.THURSDAY,
                            from = Time(5, 0),
                            to = Time(20, 0),
                            lunchTimeFrom = Time(7, 0),
                            lunchTimeTo = Time(17, 0)
                        ),
                        WorkDay(
                            weekDay = Calendar.FRIDAY,
                            from = Time(5, 0),
                            to = Time(20, 0)
                        )
                    )
                )
            }
            val names = listOf(
                "Ivan Ivanov",
                "Sergey Sergeev",
                "Dmitry Dmitriev",
                "Pert Pertov"
            )
            val texts = listOf(
                "The best latte I’ve had in Phnom Penh, one of the best of any I’ve had elsewhere. Nice ambiance, good decor.",
                "The best latte I’ve had in Phnom Penh",
                "The best place",
                "St. Christopher's Inn is in the best location in all of Berlin. You are within walking distance to the Spree Promenade, Museum Insel and, in the other direction you are within walking distance to the Berlin Hauptbahnhof..."
            )
            place.copy(
                id = index.toString(),
                schedule = schedule,
                rating = 3.5f,
                reviews = listOf(
                    Review(
                        author = Author(
                            name = names.random(),
                            user = true
                        ),
                        rating = 2f,
                        date = Date().time,
                        text = texts.random()
                    ),
                    Review(
                        author = Author(
                            name = names.random(),
                            user = false
                        ),
                        rating = 5f,
                        date = Date().time,
                        text = texts.random()
                    ),
                    Review(
                        author = Author(
                            name = names.random(),
                            user = false
                        ),
                        rating = 4f,
                        date = Date().time,
                        text = texts.random()
                    )
                )
            )
        }.subList(5, 9)
        val clusters = listOf(
            Cluster(GeoPoint(places[0].position.latitude, places[0].position.longitude), 3),
            Cluster(GeoPoint(places[1].position.latitude, places[1].position.longitude), 30),
            Cluster(GeoPoint(places[2].position.latitude, places[2].position.longitude), 99)
        )
        return Pair(places, emptyList())
    }
}

/**
 * This object is useless. We only add extension properties to split a list into
 * few separate files. This will help us to avoid opening one monstrous file
 * and dramatically slow down android studio while editing it
 */
internal object Places