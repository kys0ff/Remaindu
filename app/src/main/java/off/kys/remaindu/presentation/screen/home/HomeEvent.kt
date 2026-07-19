package off.kys.remaindu.presentation.screen.home

import android.content.Context
import off.kys.remaindu.domain.model.Notice

sealed class HomeEvent {
    data class AcknowledgeNotice(val notice: Notice) : HomeEvent()
    data class DeleteNotice(val notice: Notice) : HomeEvent()
    data class CheckPermissions(val context: Context) : HomeEvent()
}