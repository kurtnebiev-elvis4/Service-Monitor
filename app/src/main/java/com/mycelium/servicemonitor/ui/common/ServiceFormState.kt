package com.mycelium.servicemonitor.ui.common

/**
 * Data class that holds all state data needed by the ServiceForm.
 * Separating data from the UI component makes it easier to test and reuse.
 */
data class ServiceFormState(
    val title: String,
    val name: String,
    val url: String,
    val intervalValue: Int,
    val method: String,
    val body: String,
    val sha1Certificate: String,
    val groupName: String,
    val availableGroups: List<String>,
    val headersList: List<Pair<String, String>>,
    val saving: Boolean,
    val error: String,
    val isValid: Boolean
) {
    companion object {
        // Factory method to create an empty state
        fun empty(title: String = "") = ServiceFormState(
            title = title,
            name = "",
            url = "",
            intervalValue = 60,
            method = "GET",
            body = "",
            sha1Certificate = "",
            groupName = "",
            availableGroups = emptyList(),
            headersList = emptyList(),
            saving = false,
            error = "",
            isValid = false
        )
    }
}