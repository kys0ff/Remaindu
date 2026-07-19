package off.kys.remaindu.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import off.kys.remaindu.domain.model.Notice
import off.kys.remaindu.receiver.NoticeAlarmReceiver

object NoticeAlarmScheduler {

    fun schedule(context: Context, notice: Notice) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // If we can't schedule exact alarms, fallback to inexact or skip
                // The UI should ideally handle requesting this permission
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notice.nextTriggerAt,
                    createPendingIntent(context, notice)
                )
                return
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            notice.nextTriggerAt,
            createPendingIntent(context, notice)
        )
    }

    fun cancel(context: Context, noticeId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(createPendingIntent(context, noticeId))
    }

    private fun createPendingIntent(context: Context, notice: Notice): PendingIntent {
        return createPendingIntent(context, notice.id, notice.title, notice.message)
    }

    private fun createPendingIntent(
        context: Context, 
        id: Long, 
        title: String = "", 
        message: String = ""
    ): PendingIntent {
        val intent = Intent(context, NoticeAlarmReceiver::class.java).apply {
            putExtra("EXTRA_ID", id)
            putExtra("EXTRA_TITLE", title)
            putExtra("EXTRA_CONTENT", message)
        }
        return PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
