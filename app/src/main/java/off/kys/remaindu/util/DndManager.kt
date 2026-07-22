package off.kys.remaindu.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import off.kys.remaindu.domain.model.DndSettings
import off.kys.remaindu.domain.repository.NoticeRepository
import off.kys.remaindu.domain.repository.SettingsRepository
import off.kys.remaindu.receiver.DndEndReceiver
import off.kys.remaindu.service.ReminderFloatingWindow

class DndManager(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val noticeRepository: NoticeRepository
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    suspend fun enableDnd(durationMinutes: Int?) {
        val endTime = durationMinutes?.let {
            System.currentTimeMillis() + (it * 60 * 1000L)
        }
        
        settingsRepository.updateDndSettings(DndSettings(isEnabled = true, endTime = endTime))

        endTime?.let {
            val intent = Intent(context, DndEndReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                DND_ALARM_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        it,
                        pendingIntent
                    )
                    return@let
                }
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                it,
                pendingIntent
            )
        }
    }

    suspend fun disableDnd() {
        settingsRepository.updateDndSettings(DndSettings(isEnabled = false, endTime = null))
        
        val intent = Intent(context, DndEndReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DND_ALARM_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)

        showMissedNotices()
    }

    suspend fun showMissedNotices() {
        val dueNotices = noticeRepository.getDueNotices()
        dueNotices.forEach { notice ->
            ReminderFloatingWindow.showNotice(
                context = context,
                id = notice.id,
                title = notice.title,
                content = notice.message
            )
        }
    }

    companion object {
        private const val DND_ALARM_ID = 9999
    }
}
