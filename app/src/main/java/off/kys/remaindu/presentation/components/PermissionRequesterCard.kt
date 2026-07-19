package off.kys.remaindu.presentation.components

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import off.kys.remaindu.R

@Composable
fun PermissionRequesterCard(
    hasOverlayPermission: Boolean,
    hasAlarmPermission: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Alarm permission first — without it, notices simply won't fire on time, so it's the higher priority ask
        AnimatedVisibility(visible = !hasAlarmPermission) {
            PermissionCard(
                icon = painterResource(R.drawable.round_alarm_on_24),
                title = "Precise scheduling",
                description = "Required so notices trigger at the exact time you set — without it, alerts may be delayed.",
                critical = true,
                onConfigure = { openAlarmSettings(context) }
            )
        }
        AnimatedVisibility(visible = !hasOverlayPermission) {
            PermissionCard(
                icon = painterResource(R.drawable.round_picture_in_picture_24),
                title = "Display over other apps",
                description = "Lets high-priority alerts show full-screen even while you're using something else.",
                critical = false,
                onConfigure = { openOverlaySettings(context) }
            )
        }
    }
}

@Composable
private fun PermissionCard(
    icon: Painter,
    title: String,
    description: String,
    critical: Boolean,
    onConfigure: () -> Unit
) {
    val containerColor = if (critical)
        MaterialTheme.colorScheme.errorContainer
    else
        MaterialTheme.colorScheme.secondaryContainer

    val contentColor = if (critical)
        MaterialTheme.colorScheme.onErrorContainer
    else
        MaterialTheme.colorScheme.onSecondaryContainer

    Card(
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = contentColor
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 16.sp
                )
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onConfigure,
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
                    border = androidx.compose.foundation.BorderStroke(1.dp, contentColor.copy(alpha = 0.5f))
                ) {
                    Text("Enable", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

private fun openOverlaySettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        "package:${context.packageName}".toUri()
    ).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

private fun openAlarmSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(
            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
            "package:${context.packageName}".toUri()
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}