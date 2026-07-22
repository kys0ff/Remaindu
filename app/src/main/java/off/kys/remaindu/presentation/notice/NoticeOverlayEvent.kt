package off.kys.remaindu.presentation.notice

sealed class NoticeOverlayEvent {
    data class AddNotice(val id: Long, val title: String, val content: String) : NoticeOverlayEvent()
    data class RequestDismiss(val acknowledge: Boolean = false) : NoticeOverlayEvent()
    object DismissAll : NoticeOverlayEvent()

    data object ExitAnimationFinished : NoticeOverlayEvent()
}