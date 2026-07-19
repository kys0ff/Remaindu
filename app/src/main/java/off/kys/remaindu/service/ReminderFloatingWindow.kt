package off.kys.remaindu.service

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import off.kys.remaindu.presentation.notice.NoticeOverlayEffect
import off.kys.remaindu.presentation.notice.NoticeOverlayEvent
import off.kys.remaindu.presentation.notice.NoticeOverlayModel
import off.kys.remaindu.presentation.notice.NoticeOverlayScreen
import off.kys.remaindu.service.base.BaseOverlayService
import org.koin.android.ext.android.inject

class ReminderFloatingWindow : BaseOverlayService() {

    private val screenModel: NoticeOverlayModel by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override val notificationId: Int = 2
    override val channelId: String = "floating_dismiss_channel"
    override val channelName: String = "Alerts"

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            screenModel.effects.collect { effect ->
                if (effect is NoticeOverlayEffect.AllDismissed) {
                    stopSelf()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val id = intent?.getLongExtra("EXTRA_ID", 0L) ?: 0L
        val title = intent?.getStringExtra("EXTRA_TITLE")
        val content = intent?.getStringExtra("EXTRA_CONTENT")
        val isReadAction = intent?.getBooleanExtra("EXTRA_IS_READ", false) ?: false

        if (isReadAction || (title.isNullOrBlank() && content.isNullOrBlank())) {
            screenModel.onEvent(NoticeOverlayEvent.RequestDismiss(acknowledge = isReadAction))
            return START_NOT_STICKY
        }

        screenModel.onEvent(NoticeOverlayEvent.AddNotice(id, title.orEmpty(), content.orEmpty()))

        showOverlay {
            NoticeOverlayScreen(model = screenModel)
        }

        return START_NOT_STICKY
    }

    override fun createNotification(): Notification = NotificationCompat.Builder(this, channelId)
        .setContentTitle("Floating Alert Active")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .build()

    @SuppressLint("InlinedApi")
    override fun foregroundServiceType(): Int = ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE

    companion object {
        fun showNotice(context: Context, id: Long, title: String, content: String) {
            val intent = Intent(context, ReminderFloatingWindow::class.java).apply {
                putExtra("EXTRA_ID", id)
                putExtra("EXTRA_TITLE", title)
                putExtra("EXTRA_CONTENT", content)
            }
            ActivityCompat.startForegroundService(context, intent)
        }
    }
}
