package off.kys.remaindu.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import off.kys.remaindu.util.DndManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DndEndReceiver : BroadcastReceiver(), KoinComponent {
    private val dndManager: DndManager by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        scope.launch {
            dndManager.disableDnd()
        }
    }
}
