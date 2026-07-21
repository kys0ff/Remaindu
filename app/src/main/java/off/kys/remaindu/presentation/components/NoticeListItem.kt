package off.kys.remaindu.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import off.kys.remaindu.R
import off.kys.remaindu.domain.model.Notice
import off.kys.remaindu.util.DateTimeUtils
import off.kys.remaindu.util.bounceClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeListItem(
    notice: Notice,
    onEdit: (Notice) -> Unit,
    onDelete: (Notice) -> Unit,
    modifier: Modifier = Modifier,
    isResetRequested: Boolean = false
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete(notice)
        }
    }

    LaunchedEffect(isResetRequested) {
        if (isResetRequested && dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            dismissState.reset()
        }
    }

    val progress = dismissState.progress.coerceIn(0f, 1f)
    val isSwiping = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart

    val borderColor by animateColorAsState(
        targetValue = if (isSwiping && progress > 0.5f) {
            MaterialTheme.colorScheme.error.copy(alpha = (progress * 0.8f).coerceAtMost(0.8f))
        } else {
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0f)
        },
        label = "BorderColor"
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val color = if (isSwiping) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = progress)
            } else {
                MaterialTheme.colorScheme.surface
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(R.drawable.round_delete_outline_24),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = progress)
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .bounceClick(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
            border = BorderStroke(
                width = (1.dp * progress).coerceAtLeast(0.1.dp),
                color = borderColor
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(R.drawable.round_notifications_24),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = notice.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = notice.repetitionType.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "  ·  ${DateTimeUtils.formatRelative(notice.nextTriggerAt)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = { onEdit(notice) }) {
                    Icon(
                        painterResource(R.drawable.round_edit_24),
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}