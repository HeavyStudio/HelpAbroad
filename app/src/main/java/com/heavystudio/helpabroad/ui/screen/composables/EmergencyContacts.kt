package com.heavystudio.helpabroad.ui.screen.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.data.model.EmergencyCategoryKeys
import com.heavystudio.helpabroad.data.model.EmergencyContact
import com.heavystudio.helpabroad.utils.IconsUtils

@Composable
fun EmergencyContactsList(
    contacts: List<EmergencyContact>,
    onCall: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(contacts, key = { it.number + it.typeKey }) { contact ->
            EmergencyContactItem(contact = contact, onCall = onCall)
        }
    }
}

@Composable
fun EmergencyContactItem(
    contact: EmergencyContact,
    onCall: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onCall(contact.number) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = IconsUtils.getIconForType(contact.typeKey),
                    contentDescription = contact.typeKey,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = getDisplayableCategoryName(contact),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = contact.number,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                    )
                }
            }
            IconButton(onClick = { onCall(contact.number) }) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = stringResource(
                        R.string.call_action_description,
                        contact.number
                    ),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun getDisplayableCategoryName(contact: EmergencyContact): String {
    val typeKey = contact.typeKey
    val number = contact.number

    return when (typeKey) {
        EmergencyCategoryKeys.POLICE -> stringResource(R.string.category_police)
        EmergencyCategoryKeys.AMBULANCE -> stringResource(R.string.category_ambulance)
        EmergencyCategoryKeys.FIRE_DEPT -> stringResource(R.string.category_fire_dept)
        EmergencyCategoryKeys.MIEC -> stringResource(R.string.category_miec)
        EmergencyCategoryKeys.AIEC -> stringResource(R.string.category_aiec)
        EmergencyCategoryKeys.EUROPE_GENERAL -> stringResource(R.string.category_europe_general)
        EmergencyCategoryKeys.NORTH_AMERICA_GENERAL -> stringResource(R.string.category_na_general)
        EmergencyCategoryKeys.UK_GENERAL -> stringResource(R.string.category_uk_general)
        EmergencyCategoryKeys.AUSTRALIA_GENERAL -> stringResource(R.string.category_australia_general)

        EmergencyCategoryKeys.UNSPECIFIED -> {
            when (number) {
                "112" -> stringResource(R.string.category_europe_general)
                "911" -> stringResource(R.string.category_na_general)
                "999" -> stringResource(R.string.category_uk_general)
                "000" -> stringResource(R.string.category_australia_general)
                else -> stringResource(R.string.category_unspecified)
            }
        }
        else -> {
            if (typeKey.startsWith(EmergencyCategoryKeys.UNKNOWN_CATEGORY_PREFIX)) {
                val code = typeKey.substringAfter(EmergencyCategoryKeys.UNKNOWN_CATEGORY_PREFIX)
                stringResource(R.string.category_unknown, code)
            } else {
                typeKey
            }
        }

    }
}