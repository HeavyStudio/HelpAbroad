package com.heavystudio.helpabroad.ui.screen.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.utils.IconsUtils

@Composable
fun DynamicAppBarTitle(
    isLoading: Boolean,
    permissionRequired: Boolean,
    countryName: String?,
    countryIso: String?
) {
    val titleTextToShow: String
    val flagEmojiToShow: String?

    when {
        isLoading -> {
            titleTextToShow = stringResource(R.string.emergency_screen_title_loading)
            flagEmojiToShow = null
        }
        permissionRequired -> {
            titleTextToShow = stringResource(R.string.emergency_screen_title_default)
            flagEmojiToShow = null
        }
        countryName != null -> {
            titleTextToShow = countryName
            flagEmojiToShow = IconsUtils.getFlagEmojiForCountryCode(countryIso)
        }
        else -> {
            titleTextToShow = stringResource(R.string.emergency_screen_title_default)
            flagEmojiToShow = null
        }
    }

    Row {
        Text(
            text = titleTextToShow,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (!flagEmojiToShow.isNullOrEmpty()) {
            Text(
                text = flagEmojiToShow,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}