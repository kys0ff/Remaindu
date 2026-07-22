package off.kys.remaindu.presentation.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.remaindu.R
import off.kys.remaindu.domain.model.DndSettings
import off.kys.remaindu.presentation.components.EmptyState
import off.kys.remaindu.presentation.components.NoticeBanner
import off.kys.remaindu.presentation.components.NoticeListItem
import off.kys.remaindu.presentation.components.PermissionRequesterCard
import off.kys.remaindu.presentation.screen.create.CreateNoticeScreen
import off.kys.remaindu.util.bounceClick
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<HomeScreenModel>()
        val state by screenModel.state.collectAsState()
        val context = LocalContext.current
        val lazyListState = rememberLazyListState()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        val isExpanded by remember {
            derivedStateOf {
                lazyListState.firstVisibleItemIndex == 0
            }
        }

        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            screenModel.onEvent(HomeEvent.CheckPermissions(context))
        }

        if (state.noticeToDelete != null) {
            AlertDialog(
                onDismissRequest = { screenModel.onEvent(HomeEvent.DismissDeleteConfirmation) },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.round_warning_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = { Text("Delete notice?") },
                text = { Text("Are you sure you want to delete \"${state.noticeToDelete?.title}\"? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            state.noticeToDelete?.let {
                                screenModel.onEvent(HomeEvent.DeleteNotice(it))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { screenModel.onEvent(HomeEvent.DismissDeleteConfirmation) }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (state.showDndOptions) {
            DndOptionsDialog(
                selectedDuration = state.selectedDndDuration,
                onDismiss = { screenModel.onEvent(HomeEvent.ToggleDndOptions(false)) },
                onSelect = { screenModel.onEvent(HomeEvent.SelectDndDuration(it)) },
                onConfirm = { screenModel.onEvent(HomeEvent.SetDnd(it)) }
            )
        }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painterResource(if (state.dndSettings.isCurrentlyActive) R.drawable.round_notifications_none_24 else R.drawable.round_notifications_active_24),
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp),
                                    tint = if (state.dndSettings.isCurrentlyActive) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    "Remaindu",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (state.dueNotices.isNotEmpty()) {
                                Text(
                                    text = "${state.dueNotices.size} due right now",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = 40.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                if (state.dndSettings.isCurrentlyActive) {
                                    screenModel.onEvent(HomeEvent.DisableDnd)
                                } else {
                                    screenModel.onEvent(HomeEvent.ToggleDndOptions(true))
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(if (state.dndSettings.isCurrentlyActive) R.drawable.round_notifications_none_24 else R.drawable.round_notifications_active_24),
                                contentDescription = "Do Not Disturb",
                                tint = if (state.dndSettings.isCurrentlyActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    expanded = isExpanded,
                    text = { Text("New notice") },
                    icon = {
                        Icon(
                            painterResource(R.drawable.round_add_24),
                            contentDescription = null
                        )
                    },
                    onClick = { navigator.push(CreateNoticeScreen()) },
                    modifier = Modifier.bounceClick()
                )
            }
        ) { padding ->
            val upcoming = state.allNotices.filter { notice ->
                !notice.isDue && notice.isActive
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.dndSettings.isCurrentlyActive) {
                    item(key = "dnd_status") {
                        DndStatusBanner(
                            settings = state.dndSettings,
                            onDisable = { screenModel.onEvent(HomeEvent.DisableDnd) }
                        )
                    }
                }

                if (!state.hasOverlayPermission || !state.hasAlarmPermission) {
                    item(key = "permission_panel") {
                        PermissionRequesterCard(
                            hasOverlayPermission = state.hasOverlayPermission,
                            hasAlarmPermission = state.hasAlarmPermission
                        )
                    }
                }

                if (state.allNotices.isEmpty() && !state.isLoading) {
                    item(key = "empty_state") {
                        EmptyState()
                    }
                    return@LazyColumn
                }

                if (state.dueNotices.isNotEmpty()) {
                    item(key = "due_header") {
                        SectionHeader(
                            text = "Due now",
                            count = state.dueNotices.size,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(state.dueNotices, key = { "due_${it.id}" }) { notice ->
                        NoticeBanner(
                            notice = notice,
                            onAcknowledge = { screenModel.onEvent(HomeEvent.AcknowledgeNotice(it)) },
                            modifier = Modifier.animateItem()
                        )
                    }
                    item(key = "due_spacer") { Spacer(Modifier.height(4.dp)) }
                }

                if (upcoming.isNotEmpty()) {
                    item(key = "upcoming_header") {
                        SectionHeader(
                            text = "Scheduled",
                            count = upcoming.size,
                            color = MaterialTheme.colorScheme.secondary,
                            paddingTop = if (state.dueNotices.isNotEmpty()) 8.dp else 0.dp
                        )
                    }
                    items(upcoming, key = { "all_${it.id}" }) { notice ->
                        NoticeListItem(
                            notice = notice,
                            onEdit = { navigator.push(CreateNoticeScreen(it.id)) },
                            onDelete = { screenModel.onEvent(HomeEvent.RequestDeleteNotice(it)) },
                            modifier = Modifier.animateItem(),
                            isResetRequested = state.noticeToDelete == null
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun DndOptionsDialog(
        onDismiss: () -> Unit,
        onSelect: (Int?) -> Unit,
        onConfirm: (Int?) -> Unit,
        selectedDuration: Int? = null
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.round_notifications_none_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(text = "Do Not Disturb")
            },
            text = {
                val options = remember {
                    listOf(
                        Triple("30 minutes", "Mute for half an hour", 30),
                        Triple("1 hour", "Mute for sixty minutes", 60),
                        Triple("2 hours", "Mute for two hours", 120),
                        Triple("Until turned off", "Stays muted indefinitely", null)
                    )
                }

                Column(
                    modifier = Modifier.selectableGroup()
                ) {
                    options.forEach { (title, subtitle, duration) ->
                        val isSelected = selectedDuration == duration

                        ListItem(
                            headlineContent = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingContent = {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = null
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .selectable(
                                    selected = isSelected,
                                    onClick = { onSelect(duration) },
                                    role = Role.RadioButton
                                )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { onConfirm(selectedDuration) },
                    enabled = true
                ) {
                    Text("Enable")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }

    @Composable
    private fun DndStatusBanner(
        settings: DndSettings,
        onDisable: () -> Unit
    ) {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.round_notifications_none_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Do Not Disturb is active",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    settings.endTime?.let {
                        val timeStr =
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it))
                        Text(
                            "Until $timeStr",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    } ?: Text(
                        "Until manually turned off",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
                TextButton(onClick = onDisable) {
                    Text("Turn off")
                }
            }
        }
    }

    @Composable
    private fun SectionHeader(
        text: String,
        count: Int,
        color: Color,
        paddingTop: Dp = 0.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp, top = paddingTop)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = color
            )
            Badge(
                containerColor = color.copy(alpha = 0.15f),
                contentColor = color
            ) {
                Text(count.toString(), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}