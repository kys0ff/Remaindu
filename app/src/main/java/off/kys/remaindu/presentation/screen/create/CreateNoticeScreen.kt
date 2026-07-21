package off.kys.remaindu.presentation.screen.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.remaindu.R
import off.kys.remaindu.domain.model.RepetitionType
import off.kys.remaindu.presentation.components.RepetitionSelector
import org.koin.core.parameter.parametersOf

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import off.kys.remaindu.util.SmoothDuration
import off.kys.remaindu.util.bounceClick

class CreateNoticeScreen(private val noticeId: Long? = null) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<CreateNoticeScreenModel>(
            parameters = { parametersOf(noticeId) }
        )
        val state by screenModel.state.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.effects.collect { effect ->
                when (effect) {
                    CreateNoticeEffect.NoticeSaved -> navigator.pop()
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = if (state.isEditing) "Edit notice" else "New notice") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                painterResource(R.drawable.round_arrow_back_24),
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(SmoothDuration)) + slideInVertically(tween(SmoothDuration)) { it / 2 }
                ) {
                    SectionCard(
                        icon = painterResource(R.drawable.round_title_24),
                        title = "Details"
                    ) {
                        OutlinedTextField(
                            value = state.title,
                            onValueChange = { screenModel.onEvent(CreateNoticeEvent.TitleChanged(it)) },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium
                        )

                        OutlinedTextField(
                            value = state.message,
                            onValueChange = { screenModel.onEvent(CreateNoticeEvent.MessageChanged(it)) },
                            label = { Text("What do you want to remember?") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = MaterialTheme.shapes.medium
                        )
                    }
                }

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(SmoothDuration)) + slideInVertically(tween(SmoothDuration)) { it / 2 }
                ) {
                    SectionCard(
                        icon = painterResource(R.drawable.round_repeat_24),
                        title = "Frequency"
                    ) {
                        RepetitionSelector(
                            selected = state.repetitionType,
                            customIntervalMinutes = state.customIntervalMinutes,
                            oneTimeDelaySeconds = state.oneTimeDelaySeconds,
                            onSelect = { screenModel.onEvent(CreateNoticeEvent.RepetitionChanged(it)) },
                            onCustomIntervalChange = { screenModel.onEvent(CreateNoticeEvent.CustomIntervalChanged(it)) },
                            onOneTimeDelayChange = { screenModel.onEvent(CreateNoticeEvent.OneTimeDelayChanged(it)) }
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                ),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_notifications_24),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(18.dp),
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                            Column {
                                Text(
                                    text = "First reminder",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = getFirstShownText(
                                        state.repetitionType,
                                        state.customIntervalMinutes,
                                        state.oneTimeDelaySeconds
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = { screenModel.onEvent(CreateNoticeEvent.SaveNotice) },
                    enabled = state.isValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .bounceClick(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_save_24),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (state.isEditing) "Save changes" else "Save notice",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }

    @Composable
    private fun SectionCard(
        icon: Painter,
        title: String,
        content: @Composable ColumnScope.() -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                content()
            }
        }
    }

    private fun getFirstShownText(
        type: RepetitionType,
        customMinutes: String,
        oneTimeDelaySeconds: String
    ): String {
        return when (type) {
            RepetitionType.ONCE -> {
                val secs = oneTimeDelaySeconds.toLongOrNull() ?: 10L
                when {
                    secs < 60 -> "In $secs seconds"
                    secs % 3600 == 0L -> "In ${secs / 3600} hours"
                    secs % 60 == 0L -> "In ${secs / 60} minutes"
                    else -> "In ${secs / 60}m ${secs % 60}s"
                }
            }

            RepetitionType.HOURLY -> "In 1 hour"
            RepetitionType.DAILY -> "In 1 day"
            RepetitionType.WEEKLY -> "In 1 week"
            RepetitionType.MONTHLY -> "In 1 month"
            RepetitionType.CUSTOM -> {
                val mins = customMinutes.toLongOrNull() ?: 60L
                if (mins < 60) "In $mins minutes"
                else "In ${mins / 60} hours"
            }
        }
    }
}