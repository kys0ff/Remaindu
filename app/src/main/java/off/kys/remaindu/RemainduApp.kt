package off.kys.remaindu

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import off.kys.remaindu.di.appModules
import off.kys.remaindu.worker.NoticeCheckWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class RemainduApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@RemainduApp)
            modules(appModules)
        }

        schedulePeriodicDueCheck()
    }

    /** Backstop so due notices still surface (as a system notification) when
     *  the app isn't open; the in-app screen is always re-synced on launch. */
    private fun schedulePeriodicDueCheck() {
        val request = PeriodicWorkRequestBuilder<NoticeCheckWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            NoticeCheckWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
