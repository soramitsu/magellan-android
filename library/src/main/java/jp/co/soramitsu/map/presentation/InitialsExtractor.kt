package jp.co.soramitsu.map.presentation

object InitialsExtractor {

    fun extract(fullName: String): String {
        val firstCharsArray = fullName
            .split(" ")
            .filter { word -> word.isNotBlank() }
            .map { word -> word[0] }

        // ['J', 'M', 'L'] -> "JML" -> "JM"
        // ['J', 'M'] -> "JM" -> "JM"
        // ['J'] -> "J" -> "J"
        return firstCharsArray.joinToString(separator = "").plus("  ").substring(0, 2).trim()
    }
}
