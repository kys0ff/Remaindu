package off.kys.remaindu.presentation.notice

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import off.kys.remaindu.domain.usecase.NoticeUseCases
import off.kys.remaindu.presentation.core.BaseScreenModel
import kotlin.time.Duration.Companion.milliseconds

class NoticeOverlayModel(
    private val useCases: NoticeUseCases
) : BaseScreenModel<NoticeOverlayUiState, NoticeOverlayEvent, NoticeOverlayEffect>(
    NoticeOverlayUiState()
) {

    override fun onEvent(event: NoticeOverlayEvent) {
        when (event) {
            is NoticeOverlayEvent.AddNotice -> addNotice(event.id, event.title, event.content)
            is NoticeOverlayEvent.RequestDismiss -> requestDismiss(event.acknowledge)
            NoticeOverlayEvent.DismissAll -> dismissAll()
        }
    }

    private fun addNotice(id: Long, title: String, content: String) {
        val newItem = NoticeItem(id, title, content)
        updateState {
            val updatedQueue =
                if (it.noticeQueue.contains(newItem)) it.noticeQueue else it.noticeQueue.adding(
                    newItem
                )
            it.copy(
                noticeQueue = updatedQueue,
                isVisible = true
            )
        }
    }

    private fun requestDismiss(acknowledge: Boolean) {
        val idToAcknowledge = currentState.currentNotice?.id ?: 0L
        if (acknowledge && idToAcknowledge != 0L) {
            screenModelScope.launch {
                useCases.acknowledgeNotice(idToAcknowledge)
            }
        }

        updateState { it.copy(isVisible = false) }

        screenModelScope.launch {
            delay(220L.milliseconds)

            updateState {
                val newQueue =
                    if (it.noticeQueue.isNotEmpty()) it.noticeQueue.removingAt(0) else it.noticeQueue
                it.copy(
                    noticeQueue = newQueue,
                    isVisible = newQueue.isNotEmpty()
                )
            }

            if (currentState.noticeQueue.isEmpty()) {
                sendEffect(NoticeOverlayEffect.AllDismissed)
            }
        }
    }

    private fun dismissAll() {
        updateState { it.copy(isVisible = false) }
        screenModelScope.launch {
            delay(220L.milliseconds)
            updateState { it.copy(noticeQueue = persistentListOf()) }
            sendEffect(NoticeOverlayEffect.AllDismissed)
        }
    }
}
