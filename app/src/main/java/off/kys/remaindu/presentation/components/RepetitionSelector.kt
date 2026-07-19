package off.kys.remaindu.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import off.kys.remaindu.R
import off.kys.remaindu.domain.model.RepetitionType
import kotlin.math.roundToInt

private data class RepetitionOption(
    val type: RepetitionType,
    val icon: Int,
    val hint: String
)

private val options = listOf(
    RepetitionOption(RepetitionType.ONCE, R.drawable.round_schedule_24, "Single alert"),
    RepetitionOption(RepetitionType.HOURLY, R.drawable.round_repeat_24, "Every hour"),
    RepetitionOption(RepetitionType.DAILY, R.drawable.round_today_24, "Every day"),
    RepetitionOption(RepetitionType.WEEKLY, R.drawable.round_calendar_view_week_24, "Every week"),
    RepetitionOption(RepetitionType.MONTHLY, R.drawable.round_calendar_month_24, "Every month"),
    RepetitionOption(RepetitionType.CUSTOM, R.drawable.round_tune_24, "Your own interval")
)

private val quickIntervals = listOf(
    15 to "15m",
    30 to "30m",
    60 to "1h",
    180 to "3h",
    720 to "12h",
    1440 to "1d"
)

@Composable
fun RepetitionSelector(
    selected: RepetitionType,
    customIntervalMinutes: String,
    onSelect: (RepetitionType) -> Unit,
    onCustomIntervalChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        options.chunked(2).forEach { rowOptions ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowOptions.forEach { option ->
                    RepetitionOptionCard(
                        option = option,
                        isSelected = selected == option.type,
                        onClick = { onSelect(option.type) },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rowOptions.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }

        AnimatedVisibility(
            visible = selected == RepetitionType.CUSTOM,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            CustomIntervalPicker(
                minutes = customIntervalMinutes,
                onMinutesChange = onCustomIntervalChange
            )
        }
    }
}

@Composable
private fun RepetitionOptionCard(
    option: RepetitionOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(84.dp)
            .selectable(
                selected = isSelected,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        border = if (isSelected)
            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        else
            null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    painter = painterResource(option.icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = option.type.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = option.hint,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_check_24),
                        contentDescription = "Selected",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomIntervalPicker(
    minutes: String,
    onMinutesChange: (String) -> Unit
) {
    val currentMinutes = minutes.toIntOrNull()?.coerceAtLeast(1) ?: 60

    Column(modifier = Modifier.padding(top = 4.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = {
                    val next = (currentMinutes - stepFor(currentMinutes)).coerceAtLeast(1)
                    onMinutesChange(next.toString())
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Icon(
                    painter = painterResource(R.drawable.round_remove_24),
                    contentDescription = "Decrease"
                )
            }

            OutlinedTextField(
                value = minutes,
                onValueChange = { if (it.all(Char::isDigit)) onMinutesChange(it) },
                label = { Text("Minutes") },
                supportingText = { Text(formatMinutesHint(currentMinutes.toLong())) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            IconButton(
                onClick = {
                    val next = currentMinutes + stepFor(currentMinutes)
                    onMinutesChange(next.toString())
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Icon(
                    painter = painterResource(R.drawable.round_add_24),
                    contentDescription = "Increase"
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Slider(
            value = currentMinutes.toFloat().coerceIn(1f, 1440f),
            onValueChange = { onMinutesChange(it.roundToInt().coerceAtLeast(1).toString()) },
            valueRange = 1f..1440f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Quick pick",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickIntervals.forEach { (mins, label) ->
                val isActive = minutes == mins.toString()
                AssistChip(
                    onClick = { onMinutesChange(mins.toString()) },
                    label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isActive)
                            MaterialTheme.colorScheme.secondaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceContainerHigh,
                        labelColor = if (isActive)
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = null,
                    shape = MaterialTheme.shapes.small
                )
            }
        }
    }
}

/** Bigger steps once the value is large, so +/- doesn't take forever to reach useful ranges. */
private fun stepFor(current: Int): Int = when {
    current < 60 -> 5
    current < 180 -> 15
    current < 720 -> 30
    else -> 60
}

private fun formatMinutesHint(mins: Long): String = when {
    mins < 60 -> "Repeats every $mins minute${if (mins == 1L) "" else "s"}"
    mins % 1440 == 0L -> {
        val days = mins / 1440
        "Repeats every $days day${if (days == 1L) "" else "s"}"
    }

    mins % 60 == 0L -> {
        val hours = mins / 60
        "Repeats every $hours hour${if (hours == 1L) "" else "s"}"
    }

    else -> {
        val hours = mins / 60
        val rem = mins % 60
        "Repeats every ${hours}h ${rem}m"
    }
}