package off.kys.remaindu.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import off.kys.remaindu.domain.model.RepetitionType

@Entity(tableName = "notices")
data class NoticeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val repetitionType: RepetitionType,
    val customIntervalMinutes: Long?,
    val createdAt: Long,
    val nextTriggerAt: Long,
    val lastAcknowledgedAt: Long?,
    val isActive: Boolean
)
