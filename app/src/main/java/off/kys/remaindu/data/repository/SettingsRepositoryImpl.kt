package off.kys.remaindu.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import off.kys.remaindu.domain.model.DndSettings
import off.kys.remaindu.domain.repository.SettingsRepository

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    override fun observeDndSettings(): Flow<DndSettings> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_DND_ENABLED || key == KEY_DND_END_TIME) {
                trySend(getDndSettings())
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.onStart { emit(getDndSettings()) }

    override fun getDndSettings(): DndSettings {
        return DndSettings(
            isEnabled = prefs.getBoolean(KEY_DND_ENABLED, false),
            endTime = prefs.getLong(KEY_DND_END_TIME, -1L).takeIf { it != -1L }
        )
    }

    override suspend fun updateDndSettings(settings: DndSettings) {
        prefs.edit().apply {
            putBoolean(KEY_DND_ENABLED, settings.isEnabled)
            putLong(KEY_DND_END_TIME, settings.endTime ?: -1L)
            apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "remaindu_settings"
        private const val KEY_DND_ENABLED = "dnd_enabled"
        private const val KEY_DND_END_TIME = "dnd_end_time"
    }
}
