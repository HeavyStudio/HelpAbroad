package com.heavystudio.helpabroad.ui.widget

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A [GlanceAppWidgetReceiver] for the [EmergencyWidget].
 *
 * This class is responsible for handling broadcasts related to the emergency widget.
 * It specifically listens for the custom action "com.heavystudio.helpabroad.action.UPDATE_WIDGET"
 * to trigger a manual update of all placed emergency widgets. This is useful for refreshing
 * the widget's state from outside the standard widget update cycle, for example, after
 * a setting has changed in the main application.
 *
 * @author Heavy Studio.
 * @since WIP, coming soon!
 */
class EmergencyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EmergencyWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == "com.heavystudio.helpabroad.action.UPDATE_WIDGET") {
            val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
            coroutineScope.launch {
                val glanceAppWidgetManager = GlanceAppWidgetManager(context)
                val glanceIds = glanceAppWidgetManager.getGlanceIds(EmergencyWidget::class.java)
                glanceIds.forEach { glanceId ->
                    glanceAppWidget.update(context, glanceId)
                }
            }
        }
    }
}