package off.kys.remaindu.presentation.screen.home

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import off.kys.remaindu.domain.model.Notice

data class HomeUiState(
    val dueNotices: PersistentList<Notice> = persistentListOf(),
    val allNotices: PersistentList<Notice> = persistentListOf(),
    val noticeToDelete: Notice? = null,
    val isLoading: Boolean = true,
    val hasOverlayPermission: Boolean = true,
    val hasAlarmPermission: Boolean = true
)