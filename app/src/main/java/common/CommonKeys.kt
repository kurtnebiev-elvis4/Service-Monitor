package common

object CommonKeys {
    val REQUEST_CODE_POST_NOTIFICATIONS = 1001

    val predefinedKeys = listOf(
        "Content-Type", "Authorization", "Accept", "User-Agent", "Cache-Control",
        "x-api-key", "wallet-address", "wallet-signature"
    )

    val intervalOptions = mapOf(
        "15 minute" to 15,
        "30 minute" to 30,
        "1 hour" to 60,
        "3 hours" to 180,
        "12 hours" to 720,
        "1 day" to 1440,
        "1 week" to 10080
    )
    val intervalOptionsInvert = intervalOptions.entries.associate { (key, value) -> value to key }
}