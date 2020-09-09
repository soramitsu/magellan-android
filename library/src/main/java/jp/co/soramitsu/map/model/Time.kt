package jp.co.soramitsu.map.model

data class Time(
    val hour: Int,
    val minute: Int
) {

    fun inMilliseconds(): Long = inMinutes().toLong() * MILLISECONDS_IN_MINUTE

    private fun inMinutes() = hour * 60 + minute

    operator fun compareTo(other: Time): Int = this.inMinutes().compareTo(other.inMinutes())

    private companion object {
        private const val MILLISECONDS_IN_MINUTE = 1000 * 60
    }
}
