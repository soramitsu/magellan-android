package jp.co.soramitsu.map.model

data class Category(
    val id: Int,
    val name: String
) {
    companion object {
        val BANK = Category(-1, "bank")
        val FOOD = Category(-1, "food")
        val SERVICES = Category(-1, "services")
        val SUPERMARKETS = Category(-1, "supermarkets")
        val PHARMACY = Category(-1, "pharmacy")
        val ENTERTAINMENT = Category(-1, "entertainment")
        val EDUCATION = Category(-1, "education")
        val OTHER = Category(-1, "other")
    }
}
