package jp.co.soramitsu.map.model

data class Category(
    val id: Long,
    val name: String
) {
    companion object {
        val BANK = Category(-1, "bank")
        val FOOD = Category(-2, "food")
        val SERVICES = Category(-3, "services")
        val SUPERMARKETS = Category(-4, "supermarkets")
        val PHARMACY = Category(-5, "pharmacy")
        val ENTERTAINMENT = Category(-6, "entertainment")
        val EDUCATION = Category(-7, "education")
        val OTHER = Category(-8, "other")
    }
}
