package off.kys.remaindu.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import off.kys.remaindu.data.local.RemainduDatabase
import off.kys.remaindu.service.ReminderFloatingWindow

/** Periodically checks for notices that became due in the background and
 *  surfaces a system notification for each; the in-app list re-syncs on open. */
class NoticeCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dao = RemainduDatabase.getInstance(applicationContext).noticeDao()
        val due = dao.getDueOnce(System.currentTimeMillis())
        due.forEach { notice ->
            ReminderFloatingWindow.showNotice(
                context = this.applicationContext,
                id = notice.id,
                title = notice.title,
                content = notice.message
            )
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "notice_check_worker"
    }
}
