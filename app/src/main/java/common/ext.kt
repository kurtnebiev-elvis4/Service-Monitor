package common

// Helper to parse header string into list of key-value pairs.
fun parseHeaders(headers: String): List<Pair<String, String>> {
    if (headers.isBlank()) return emptyList()
    return headers.split(",").mapNotNull { pairStr ->
        val parts = pairStr.split(":")
        if (parts.size >= 2) Pair(parts[0].trim(), parts[1].trim()) else null
    }
}
