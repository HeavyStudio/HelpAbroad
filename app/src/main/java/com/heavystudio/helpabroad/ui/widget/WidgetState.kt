package com.heavystudio.helpabroad.ui.widget

/**
 * Represents the state of the app widget.
 *
 * This data class holds all the necessary information to display the widget's content,
 * including the name of the selected country and a list of emergency services available there.
 *
 * @property countryName The name of the country for which the services are displayed.
 * @property services A list of emergency services. Each service is a Pair, where the
 *                    first string is the service name (e.g., "Police") and the second
 *                    string is the corresponding phone number (e.g., "112").
 *
 * @author Heavy Studio.
 * @since WIP, coming soon!
 */
data class WidgetState(
    val countryName: String,
    val services: List<Pair<String, String>>
)
