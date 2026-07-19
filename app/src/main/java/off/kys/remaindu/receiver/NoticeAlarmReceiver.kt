package off.kys.remaindu.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import off.kys.remaindu.service.ReminderFloatingWindow

class NoticeAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra("EXTRA_ID", 0L)
        val title = intent.getStringExtra("EXTRA_TITLE") ?: ""
        val content = intent.getStringExtra("EXTRA_CONTENT") ?: ""

        if (id != 0L) {
            ReminderFloatingWindow.showNotice(context, id, title, content)
        }
    }
}
