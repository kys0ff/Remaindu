package off.kys.remaindu.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import off.kys.remaindu.data.local.NoticeDao
import off.kys.remaindu.data.local.NoticeEntity
import off.kys.remaindu.domain.model.Notice
import off.kys.remaindu.domain.model.RepetitionType
import off.kys.remaindu.domain.repository.NoticeRepository
import off.kys.remaindu.util.DateTimeUtils
import off.kys.remaindu.util.NoticeAlarmScheduler

class NoticeRepositoryImpl(
    private val context: Context,
    private val dao: NoticeDao
) : NoticeRepository {

    override fun observeAll(): Flow<List<Notice>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeDue(): Flow<List<Notice>> =
        dao.observeDue(System.currentTimeMillis()).map { list -> list.map { it.toDomain() } }

    override suspend fun getDueNotices(): List<Notice> =
        dao.getDueOnce(System.currentTimeMillis()).map { it.toDomain() }

    override suspend fun getById(id: Long): Notice? =
        dao.getById(id)?.toDomain()

    override suspend fun add(notice: Notice): Long {
        val id = dao.upsert(notice.toEntity())
        val saved = notice.copy(id = id)
        NoticeAlarmScheduler.schedule(context, saved)
        return id
    }

    override suspend fun update(notice: Notice) {
        dao.update(notice.toEntity())
        if (notice.isActive) {
            NoticeAlarmScheduler.schedule(context, notice)
        } else {
            NoticeAlarmScheduler.cancel(context, notice.id)
        }
    }

    override suspend fun delete(id: Long) {
        dao.delete(id)
        NoticeAlarmScheduler.cancel(context, id)
    }

    override suspend fun acknowledge(id: Long) {
        val entity = dao.getById(id) ?: return
        val now = System.currentTimeMillis()
        if (entity.repetitionType == RepetitionType.ONCE) {
            val updated = entity.copy(isActive = false, lastAcknowledgedAt = now)
            dao.update(updated)
            NoticeAlarmScheduler.cancel(context, id)
        } else {
            val next = DateTimeUtils.nextTrigger(
                from = now,
                type = entity.repetitionType,
                customIntervalMinutes = entity.customIntervalMinutes
            )
            val updated = entity.copy(nextTriggerAt = next, lastAcknowledgedAt = now)
            dao.update(updated)
            NoticeAlarmScheduler.schedule(context, updated.toDomain())
        }
    }
}

private fun NoticeEntity.toDomain() = Notice(
    id = id,
    title = title,
    message = message,
    repetitionType = repetitionType,
    customIntervalMinutes = customIntervalMinutes,
    createdAt = createdAt,
    nextTriggerAt = nextTriggerAt,
    lastAcknowledgedAt = lastAcknowledgedAt,
    isActive = isActive
)

private fun Notice.toEntity() = NoticeEntity(
    id = id,
    title = title,
    message = message,
    repetitionType = repetitionType,
    customIntervalMinutes = customIntervalMinutes,
    createdAt = createdAt,
    nextTriggerAt = nextTriggerAt,
    lastAcknowledgedAt = lastAcknowledgedAt,
    isActive = isActive
)
