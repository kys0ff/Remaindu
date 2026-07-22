package off.kys.remaindu.domain.repository

import kotlinx.coroutines.flow.Flow
import off.kys.remaindu.domain.model.DndSettings

interface SettingsRepository {
    fun observeDndSettings(): Flow<DndSettings>
    fun getDndSettings(): DndSettings
    suspend fun updateDndSettings(settings: DndSettings)
}
