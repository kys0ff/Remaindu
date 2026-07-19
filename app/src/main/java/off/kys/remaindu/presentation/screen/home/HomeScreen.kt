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
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.remaindu.R
import off.kys.remaindu.presentation.components.EmptyState
import off.kys.remaindu.presentation.components.NoticeBanner
import off.kys.remaindu.presentation.components.NoticeListItem
import off.kys.remaindu.presentation.components.PermissionRequesterCard
import off.kys.remaindu.presentation.screen.create.CreateNoticeScreen

class HomeScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<HomeScreenModel>()
        val state by screenModel.state.collectAsState()
        val context = LocalContext.current

        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            screenModel.onEvent(HomeEvent.CheckPermissions(context))
        }

        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painterResource(R.drawable.round_notifications_active_24),
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(10.dp))
                                Text("Remaindu")
                            }
                            if (state.dueNotices.isNotEmpty()) {
                                Text(
                                    text = "${state.dueNotices.size} due right now",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = 40.dp)
                                )
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("New notice") },
                    icon = {
                        Icon(
                            painterResource(R.drawable.round_add_24),
                            contentDescription = null
                        )
                    },
                    onClick = { navigator.push(CreateNoticeScreen()) }
                )
            }
        ) { padding ->
            val upcoming = state.allNotices.filter { notice ->
                !notice.isDue && notice.isActive
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                            onDelete = { screenModel.onEvent(HomeEvent.DeleteNotice(it)) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun SectionHeader(
        text: String,
        count: Int,
        color: androidx.compose.ui.graphics.Color,
        paddingTop: androidx.compose.ui.unit.Dp = 0.dp
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