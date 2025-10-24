package com.heavystudio.helpabroad.ui.disclaimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.heavystudio.helpabroad.R

/**
 * A Composable screen that displays the application's disclaimer.
 *
 * This screen presents important information to the user regarding the accuracy of the data,
 * its use in emergencies, and the developers' liability. The content is structured in several
 * sections for readability, including an introduction and specific points on accuracy,
 * emergency use, and liability. The layout is a vertically scrollable list.
 *
 * @param navController The [NavController] used for navigation actions, although currently not used
 *                      within this composable's content, it's passed for potential future use like
 *                      a back button.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisclaimerScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Introduction
        item {
            Text(
                text = stringResource(R.string.disclaimer_intro),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }

        // Section 1 : Exactitude
        item {
            DisclaimerSection(
                title = stringResource(id = R.string.disclaimer_accuracy_title),
                body = stringResource(id = R.string.disclaimer_accuracy_body)
            )
        }

        // Section 2 : Usage en urgence
        item {
            DisclaimerSection(
                title = stringResource(id = R.string.disclaimer_emergency_title),
                body = stringResource(id = R.string.disclaimer_emergency_body)
            )
        }

        // Section 3 : Responsabilité
        item {
            DisclaimerSection(
                title = stringResource(id = R.string.disclaimer_liability_title),
                body = stringResource(id = R.string.disclaimer_liability_body)
            )
        }
    }
}

@Composable
private fun DisclaimerSection(title: String, body: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 24.sp
        )
    }
}