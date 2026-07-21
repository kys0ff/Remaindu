package off.kys.remaindu.presentation.notice

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import off.kys.remaindu.R
import kotlin.math.absoluteValue

@Composable
fun NoticeOverlayScreen(
    model: NoticeOverlayModel
) {
    val state by model.state.collectAsState()
    val currentNotice = state.currentNotice ?: return
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = state.isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(tween(200)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + scaleOut(
            targetScale = 0.9f,
            animationSpec = tween(200, easing = FastOutSlowInEasing)
        ) + fadeOut(tween(150))
    ) {
        val dragOffsetY = remember { Animatable(0f) }
        val dismissThreshold = -100f
        val isBeyondThreshold = dragOffsetY.value < dismissThreshold

        val borderColor by animateColorAsState(
            targetValue = if (isBeyondThreshold) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            },
            label = "BorderColorAnimation"
        )

        val borderStroke = if (dragOffsetY.value < 0f) {
            val progress = (dragOffsetY.value.absoluteValue / 150f).coerceIn(0f, 1f)
            BorderStroke(
                width = (1.dp + (2.dp * progress)),
                color = borderColor
            )
        } else null

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 500.dp)
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = dragOffsetY.value.coerceAtMost(0f)
                        val progress =
                            (dragOffsetY.value.absoluteValue / 300f).coerceIn(0f, 1f)
                        alpha = 1f - progress * 0.4f
                        scaleX = 1f - progress * 0.05f
                        scaleY = 1f - progress * 0.05f
                    }
                    .pointerInput(currentNotice.id) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                if (dragOffsetY.value < dismissThreshold) {
                                    model.onEvent(NoticeOverlayEvent.RequestDismiss())
                                } else {
                                    scope.launch {
                                        dragOffsetY.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                                    }
                                }
                            },
                            onDragCancel = {
                                scope.launch {
                                    dragOffsetY.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                                }
                            },
                            onVerticalDrag = { change, delta ->
                                change.consume()
                                scope.launch {
                                    // Apply resistance when dragging up
                                    val newOffset = dragOffsetY.value + delta
                                    dragOffsetY.snapTo(newOffset.coerceAtMost(0f))
                                }
                            }
                        )
                    }
                    .shadow(
                        elevation = if (dragOffsetY.value < 0f) 2.dp else 8.dp,
                        shape = RoundedCornerShape(28.dp)
                    ),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
                border = borderStroke
            ) {
                Column(modifier = Modifier.animateContentSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .width(32.dp)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                    }

                    Row(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 12.dp,
                            top = 10.dp,
                            bottom = 4.dp
                        ),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painterResource(R.drawable.round_notifications_24),
                                contentDescription = "Alert",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 2.dp)
                                .heightIn(max = 320.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            if (currentNotice.title.isNotEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = currentNotice.title,
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            letterSpacing = 0.15.sp
                                        )
                                    )
                                    AnimatedContent(
                                        targetState = state.noticeQueue.size,
                                        transitionSpec = {
                                            (slideInVertically { height -> height } + fadeIn())
                                                .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                                        }, label = "QueueCountBadge"
                                    ) { size ->
                                        if (size > 1) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                                shape = CircleShape
                                            ) {
                                                Text(
                                                    text = "+${size - 1} more",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(
                                                        horizontal = 8.dp,
                                                        vertical = 3.dp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            if (currentNotice.title.isNotEmpty() && currentNotice.content.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            if (currentNotice.content.isNotEmpty()) {
                                Text(
                                    text = currentNotice.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = { model.onEvent(NoticeOverlayEvent.RequestDismiss()) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                painterResource(R.drawable.round_close_24),
                                contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 12.dp,
                                top = 4.dp
                            ),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.noticeQueue.size > 1) {
                            TextButton(
                                onClick = { model.onEvent(NoticeOverlayEvent.DismissAll) },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text(
                                    "Dismiss All",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                            Spacer(Modifier.weight(1f))
                        }
                        TextButton(onClick = { model.onEvent(NoticeOverlayEvent.RequestDismiss()) }) {
                            Text(
                                "Later",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Spacer(Modifier.width(4.dp))
                        Button(
                            onClick = { model.onEvent(NoticeOverlayEvent.RequestDismiss(acknowledge = true)) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(
                                painterResource(R.drawable.round_check_circle_24),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Got it",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
