package off.kys.remaindu.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import off.kys.remaindu.domain.repository.NoticeRepository
import off.kys.remaindu.util.NoticeAlarmScheduler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val repository: NoticeRepository by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scope.launch {
                val allNotices = repository.observeAll().first()
                val now = System.currentTimeMillis()
                allNotices.filter { it.isActive && it.nextTriggerAt > now }.forEach { notice ->
                    NoticeAlarmScheduler.schedule(context, notice)
                }
            }
        }
    }
}
