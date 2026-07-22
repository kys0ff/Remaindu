package off.kys.remaindu.presentation.notice

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import off.kys.remaindu.domain.usecase.NoticeUseCases
import off.kys.remaindu.presentation.core.BaseScreenModel

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
            is NoticeOverlayEvent.ExitAnimationFinished -> handleExitFinished()
        }
    }

    private fun addNotice(id: Long, title: String, content: String) {
        val newItem = NoticeItem(id, title, content)
        updateState { state ->
            val updatedQueue = if (state.noticeQueue.contains(newItem)) {
                state.noticeQueue
            } else {
                state.noticeQueue.adding(newItem)
            }
            state.copy(noticeQueue = updatedQueue)
        }
    }

    private fun requestDismiss(acknowledge: Boolean) {
        val idToAcknowledge = currentState.currentNotice?.id ?: 0L
        if (acknowledge && idToAcknowledge != 0L) {
            screenModelScope.launch {
                useCases.acknowledgeNotice(idToAcknowledge)
            }
        }

        updateState { state ->
            val newQueue = if (state.noticeQueue.isNotEmpty()) {
                state.noticeQueue.removingAt(0)
            } else {
                state.noticeQueue
            }
            state.copy(noticeQueue = newQueue)
        }
    }

    private fun dismissAll() {
        updateState { it.copy(noticeQueue = persistentListOf()) }
    }

    private fun handleExitFinished() {
        if (currentState.noticeQueue.isEmpty()) {
            sendEffect(NoticeOverlayEffect.AllDismissed)
        }
    }
}