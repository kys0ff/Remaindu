package off.kys.remaindu.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import off.kys.remaindu.domain.repository.SettingsRepository
import off.kys.remaindu.service.ReminderFloatingWindow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NoticeAlarmReceiver : BroadcastReceiver(), KoinComponent {
    private val settingsRepository: SettingsRepository by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra("EXTRA_ID", 0L)
        val title = intent.getStringExtra("EXTRA_TITLE") ?: ""
        val content = intent.getStringExtra("EXTRA_CONTENT") ?: ""

        if (id != 0L) {
            scope.launch {
                val dndSettings = settingsRepository.getDndSettings()
                if (!dndSettings.isCurrentlyActive) {
                    ReminderFloatingWindow.showNotice(context, id, title, content)
                }
            }
        }
    }
}
