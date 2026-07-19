package off.kys.remaindu.presentation.screen.home

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import off.kys.remaindu.domain.model.Notice
import off.kys.remaindu.domain.usecase.NoticeUseCases
import off.kys.remaindu.presentation.core.BaseScreenModel
import kotlin.time.Duration.Companion.milliseconds

class HomeScreenModel(
    private val useCases: NoticeUseCases
) : BaseScreenModel<HomeUiState, HomeEvent, HomeEffect>(HomeUiState()) {

    private val ticker = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1000.milliseconds)
        }
    }

    init {
        screenModelScope.launch {
            combine(
                useCases.observeNotices(),
                ticker
            ) { all, now ->
                val due = all.filter { it.isActive && it.nextTriggerAt <= now }

                updateState { current ->
                    current.copy(
                        dueNotices = due.sortedByDescending { it.nextTriggerAt }.toPersistentList(),
                        allNotices = all.sortedBy { it.nextTriggerAt }.toPersistentList(),
                        isLoading = false
                    )
                }
            }.collect {}
        }
    }

    override fun onEvent(event: HomeEvent) = when (event) {
        is HomeEvent.AcknowledgeNotice -> acknowledge(event.notice)
        is HomeEvent.RequestDeleteNotice -> updateState { it.copy(noticeToDelete = event.notice) }
        is HomeEvent.DeleteNotice -> delete(event.notice)
        HomeEvent.DismissDeleteConfirmation -> updateState { it.copy(noticeToDelete = null) }
        is HomeEvent.CheckPermissions -> checkPermissions(event.context)
    }

    private fun checkPermissions(context: Context) {
        val overlayGranted = Settings.canDrawOverlays(context)
        val alarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        updateState {
            it.copy(
                hasOverlayPermission = overlayGranted,
                hasAlarmPermission = alarmGranted
            )
        }
    }

    private fun acknowledge(notice: Notice) {
        screenModelScope.launch { useCases.acknowledgeNotice(notice.id) }
    }

    private fun delete(notice: Notice) {
        screenModelScope.launch {
            useCases.deleteNotice(notice.id)
            updateState { it.copy(noticeToDelete = null) }
        }
    }
}
