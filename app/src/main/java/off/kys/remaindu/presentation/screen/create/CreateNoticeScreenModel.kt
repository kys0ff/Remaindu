package off.kys.remaindu.presentation.screen.create

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import off.kys.remaindu.domain.model.Notice
import off.kys.remaindu.domain.model.RepetitionType
import off.kys.remaindu.domain.usecase.NoticeUseCases
import off.kys.remaindu.presentation.core.BaseScreenModel
import off.kys.remaindu.util.DateTimeUtils

class CreateNoticeScreenModel(
    private val noticeId: Long?,
    private val useCases: NoticeUseCases
) : BaseScreenModel<CreateNoticeUiState, CreateNoticeEvent, CreateNoticeEffect>(CreateNoticeUiState()) {

    init {
        if (noticeId != null) {
            screenModelScope.launch {
                useCases.getNotice(noticeId)?.let { notice ->
                    updateState {
                        it.copy(
                            id = notice.id,
                            title = notice.title,
                            message = notice.message,
                            repetitionType = notice.repetitionType,
                            customIntervalMinutes = notice.customIntervalMinutes?.toString() ?: "60",
                            isEditing = true,
                            createdAt = notice.createdAt
                        )
                    }
                }
            }
        }
    }

    override fun onEvent(event: CreateNoticeEvent) = when (event) {
        is CreateNoticeEvent.TitleChanged -> updateState { it.copy(title = event.value) }
        is CreateNoticeEvent.MessageChanged -> updateState { it.copy(message = event.value) }
        is CreateNoticeEvent.RepetitionChanged -> updateState { it.copy(repetitionType = event.value) }
        is CreateNoticeEvent.CustomIntervalChanged -> updateState { it.copy(customIntervalMinutes = event.value) }
        CreateNoticeEvent.SaveNotice -> save()
    }

    private fun save() {
        val current = currentState
        if (!current.isValid) return
        screenModelScope.launch {
            val now = System.currentTimeMillis()
            val nextTrigger = if (current.repetitionType == RepetitionType.ONCE) {
                now + 10_000L
            } else {
                DateTimeUtils.nextTrigger(
                    from = now,
                    type = current.repetitionType,
                    customIntervalMinutes = current.customIntervalMinutes.toLongOrNull()
                )
            }

            val notice = Notice(
                id = current.id,
                title = current.title.trim(),
                message = current.message.trim(),
                repetitionType = current.repetitionType,
                customIntervalMinutes = current.customIntervalMinutes.toLongOrNull(),
                createdAt = if (current.isEditing) current.createdAt else now,
                nextTriggerAt = nextTrigger,
                isActive = true
            )

            if (current.isEditing) {
                useCases.updateNotice(notice)
            } else {
                useCases.addNotice(notice)
            }
            sendEffect(CreateNoticeEffect.NoticeSaved)
        }
    }
}
