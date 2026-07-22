package off.kys.remaindu.domain.repository

import kotlinx.coroutines.flow.Flow
import off.kys.remaindu.domain.model.Notice

interface NoticeRepository {
    fun observeAll(): Flow<List<Notice>>
    fun observeDue(): Flow<List<Notice>>
    suspend fun getDueNotices(): List<Notice>
    suspend fun getById(id: Long): Notice?
    suspend fun add(notice: Notice): Long
    suspend fun update(notice: Notice)
    suspend fun delete(id: Long)

    /**
     * Called when the user reads/taps a due notice.
     * ONCE notices are deactivated; repeating ones get their nextTriggerAt pushed forward.
     */
    suspend fun acknowledge(id: Long)
}
