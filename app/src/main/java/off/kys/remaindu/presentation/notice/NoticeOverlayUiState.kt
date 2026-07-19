package off.kys.remaindu.presentation.notice

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class NoticeOverlayUiState(
    val noticeQueue: PersistentList<NoticeItem> = persistentListOf(),
    val isVisible: Boolean = false
) {
    val currentNotice get() = noticeQueue.firstOrNull()
}