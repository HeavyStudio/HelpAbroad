package com.heavystudio.helpabroad.ui.about

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.heavystudio.helpabroad.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current
    val appVersion = getAppVersion(context)
    val packageName = context.packageName

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(R.string.app_version, appVersion),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.about_description),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
        }

        item {
            ActionItem(
                title = stringResource(R.string.about_rate_app),
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "market://details?id=${context.packageName}".toUri()
                    )
                    context.startActivity(intent)
                }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }

        item {
            ActionItem(
                title = stringResource(R.string.about_share_app),
                onClick = {
                    val playStoreLink = "https://play.google.com/store/apps/details?id=$packageName"
                    val shareText = context.getString(R.string.share_app_text, playStoreLink)
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }

        item {
            ActionItem(
                title = stringResource(R.string.about_feedback),
                onClick = {
                    val recipient = "heavystudio.dev@gmail.com"
                    val subject = "Feeback pour Help Abroad v$appVersion"

                    val uriText = "mailto:$recipient?subject=${subject.toUri()}"
                    val mailUri = uriText.toUri()

                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = mailUri
                    }

                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.no_email_app_found),
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    }
                }
            )
            HorizontalDivider()
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.translation_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.developed_by),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ActionItem(title: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(text = title) },
        modifier = Modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

private fun getAppVersion(context: Context): String {
    return try {
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "N/A"
    } catch (e: Exception) {
        e.printStackTrace()
        "N/A"
    }
}