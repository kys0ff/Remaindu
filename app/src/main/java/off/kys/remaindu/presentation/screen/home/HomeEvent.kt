package off.kys.remaindu.presentation.screen.home

import android.content.Context
import off.kys.remaindu.domain.model.Notice

sealed class HomeEvent {
    data class AcknowledgeNotice(val notice: Notice) : HomeEvent()
    data class RequestDeleteNotice(val notice: Notice) : HomeEvent()
    data class DeleteNotice(val notice: Notice) : HomeEvent()
    object DismissDeleteConfirmation : HomeEvent()
    data class CheckPermissions(val context: Context) : HomeEvent()
    data class ToggleDndOptions(val show: Boolean) : HomeEvent()
    data class SelectDndDuration(val durationMinutes: Int?) : HomeEvent()
    data class SetDnd(val durationMinutes: Int?) : HomeEvent()
    object DisableDnd : HomeEvent()
}