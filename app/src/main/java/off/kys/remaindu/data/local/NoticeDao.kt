package off.kys.remaindu.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoticeDao {

    @Query("SELECT * FROM notices ORDER BY nextTriggerAt ASC")
    fun observeAll(): Flow<List<NoticeEntity>>

    @Query("SELECT * FROM notices WHERE isActive = 1 AND nextTriggerAt <= :now ORDER BY nextTriggerAt ASC")
    fun observeDue(now: Long): Flow<List<NoticeEntity>>

    @Query("SELECT * FROM notices WHERE isActive = 1 AND nextTriggerAt <= :now")
    suspend fun getDueOnce(now: Long): List<NoticeEntity>

    @Query("SELECT * FROM notices WHERE id = :id")
    suspend fun getById(id: Long): NoticeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: NoticeEntity): Long

    @Update
    suspend fun update(entity: NoticeEntity)

    @Query("DELETE FROM notices WHERE id = :id")
    suspend fun delete(id: Long)
}
