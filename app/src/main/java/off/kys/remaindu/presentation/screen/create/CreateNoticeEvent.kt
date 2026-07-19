package off.kys.remaindu.presentation.screen.create

import off.kys.remaindu.domain.model.RepetitionType

sealed class CreateNoticeEvent {
    data class TitleChanged(val value: String) : CreateNoticeEvent()
    data class MessageChanged(val value: String) : CreateNoticeEvent()
    data class RepetitionChanged(val value: RepetitionType) : CreateNoticeEvent()
    data class CustomIntervalChanged(val value: String) : CreateNoticeEvent()
    object SaveNotice : CreateNoticeEvent()
}