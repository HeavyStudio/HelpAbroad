package com.heavystudio.helpabroad.ui.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.heavystudio.helpabroad.data.local.dto.CountryDetails
import com.heavystudio.helpabroad.di.WidgetDataEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

/**
 * A [GlanceAppWidget] that displays emergency numbers for a user-defined default country.
 *
 * This widget fetches the default country set in the application's settings. It then retrieves
 * the emergency service details for that country.
 *
 * The logic for displaying numbers is as follows:
 * 1. If "DISPATCH" (general emergency) numbers are available, they are displayed.
 * 2. If no "DISPATCH" numbers exist, it displays up to three prioritized services, typically
 *    Police, Ambulance, and Fire.
 *
 * If no default country is set or if the data cannot be fetched, the widget shows a message
 * prompting the user to set a default country in the main application.
 *
 * Tapping on a number in the widget initiates a "dial" intent, opening the user's phone app
 * with the selected number pre-filled.
 *
 * @author Heavy Studio.
 * @since WIP, coming soon!
 *
 * @see GlanceAppWidget
 * @see WidgetDataEntryPoint
 */
class EmergencyWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(context, WidgetDataEntryPoint::class.java)
        val countryRepository = entryPoint.countryRepository()
        val settingsRepository = entryPoint.settingsRepository()

        val defaultCountryId = settingsRepository.defaultCountryIdFlow.first()

        val widgetState = if (defaultCountryId != null) {
            val details: CountryDetails? = countryRepository.getCountryDetails(defaultCountryId).first()
            if (details != null) {
                val countryName = details.names.find { it.languageCode == "en" }?.name ?: details.country.isoCode

                // --- New Selection Logic ---
                val servicesForWidget: List<Pair<String, String>>

                // First, check if there is a DISPATCH-type number
                val dispatchServices = details.services.filter { it.type.serviceCode == "DISPATCH" }

                if (dispatchServices.isNotEmpty()) {
                    // CASE 1: One or more general numbers exist; display them
                    servicesForWidget = dispatchServices.mapNotNull {
                        val serviceName = it.names.find { n -> n.languageCode == "en" }?.name ?: "General"
                        Pair(serviceName, it.number.phoneNumber)
                    }
                } else {
                    // CASE 2: No general numbers, the 3 priority numbers are displayed
                    val servicePriority = mapOf("POLICE" to 1, "AMBULANCE" to 2, "SAMU" to 2, "FIRE" to 3)
                    val sortedServices = details.services.sortedBy { service ->
                        servicePriority[service.type.serviceCode] ?: 99
                    }
                    servicesForWidget = sortedServices.take(3).mapNotNull {
                        val serviceName = it.names.find { n -> n.languageCode == "en" }?.name
                        if (serviceName != null) {
                            Pair(serviceName, it.number.phoneNumber)
                        } else {
                            null
                        }
                    }
                }
                WidgetState(countryName = countryName, services = servicesForWidget)
            } else {
                null
            }
        } else {
            null
        }

        provideContent {
            GlanceTheme {
                if (widgetState != null && widgetState.services.isNotEmpty()) {
                    WidgetContent(state = widgetState)
                } else {
                    EmptyWidgetContent()
                }
            }
        }
    }
}

@Composable
private fun WidgetContent(state: WidgetState) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // En-tête
        Text(
            text = state.countryName,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = GlanceTheme.colors.primary
            )
        )
        Spacer(GlanceModifier.height(8.dp))

        state.services.forEach { service ->
            WidgetNumberItem(service = service.first, number = service.second)
        }
    }
}

@Composable
private fun EmptyWidgetContent() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Définissez un pays par défaut dans l'application.") // TODO: i18n
    }
}

@Composable
private fun WidgetNumberItem(service: String, number: String) {
    Row(
        modifier = GlanceModifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .clickable(actionRunCallback<CallAction>(
                parameters = actionParametersOf(phoneNumberKey to number)
            )),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = service,
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant
            )
        )
        Spacer(GlanceModifier.defaultWeight())
        Text(
            text = number,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = GlanceTheme.colors.onSurface
            )
        )
    }
}

private val phoneNumberKey = ActionParameters.Key<String>("phoneNumberKey")

class CallAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val phoneNumber = parameters[phoneNumberKey] ?: return

        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}