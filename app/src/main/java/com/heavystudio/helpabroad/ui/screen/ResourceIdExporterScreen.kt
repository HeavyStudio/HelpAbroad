package com.heavystudio.helpabroad.ui.screen // Or any other appropriate package

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heavystudio.helpabroad.R // Import your app's R class

// Data class to hold the information for display
data class ResourceDisplayInfo(val name: String, val id: Int, val actualValue: String)

@Composable
fun ResourceIdExporterScreen() {
    val context = LocalContext.current

    // Define the list of string resources you want to inspect
    // Add all the R.string references you need IDs for
    val stringResourcesToInspect = listOf(
        "category_security" to R.string.category_security,
        "category_medical" to R.string.category_medical,
        "category_fire_rescue" to R.string.category_fire_rescue,
        "category_dispatch_general" to R.string.category_dispatch_general,

        // Service Names - Also likely candidates for DB if you link services to categories

        "service_all_services" to R.string.service_all_services,
        "service_police" to R.string.service_police,
        "service_ambulance" to R.string.service_ambulance,
        "service_fire_department" to R.string.service_fire_department
    )

    Log.d("ResourceExporter", "--- Start Resource Inspection ---")
    val resourceDisplayList = stringResourcesToInspect.map { (name, id) ->
        val value = try {
            context.getString(id)
        } catch (e: Exception) {
            "Error loading string: ${e.message}"
        }
        Log.d("ResourceExporter", "Processing: Name='$name', ID=$id, Value='$value'")
        ResourceDisplayInfo(name, id, value)
    }
    Log.d("ResourceExporter", "--- End Resource Inspection ---")

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Resource ID Exporter",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Below are the string resource keys, their integer IDs, and their current values:",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (resourceDisplayList.isEmpty()) {
            Text("No resources specified in `stringResourcesToInspect` list.")
        } else {
            LazyColumn {
                items(resourceDisplayList) { resourceInfo: ResourceDisplayInfo ->
                    ResourceIdItem(resourceInfo)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun ResourceIdItem(info: ResourceDisplayInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Key: ${info.name}",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "ID: ${info.id}",
            fontSize = 14.sp,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Value: \"${info.actualValue}\"",
            fontSize = 14.sp,
            color = Color.DarkGray
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ResourceIdExporterScreenPreview() {
    // If your app has a specific theme, you might want to wrap this preview in it
    // HelpAbroadTheme {
    ResourceIdExporterScreen()
    // }
}
