package com.heavystudio.helpabroad.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.heavystudio.helpabroad.R

/**
 * A composable function that displays a center-aligned top app bar for the application.
 *
 * This top bar includes a title and an optional back navigation button. A horizontal divider is
 * displayed below the app bar.
 *
 * @param title The text to be displayed as the title of the app bar.
 * @param canNavigateBack A boolean value that determines whether the back navigation button is shown.
 * @param onNavigateUp A lambda function to be invoked when the back navigation button is clicked.
 *                      This is only used if [canNavigateBack] is true. Defaults to an empty lambda.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the component.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit = {}
) {
    Column {
        CenterAlignedTopAppBar(
            title = { Text(text = title) },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            ),
            navigationIcon = {
                if (canNavigateBack) {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_desc)
                        )
                    }
                }
            }
        )
        HorizontalDivider()
    }
}