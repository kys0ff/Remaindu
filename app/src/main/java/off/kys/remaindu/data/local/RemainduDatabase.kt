package off.kys.remaindu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [NoticeEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RemainduDatabase : RoomDatabase() {

    abstract fun noticeDao(): NoticeDao

    companion object {
        @Volatile private var INSTANCE: RemainduDatabase? = null

        fun getInstance(context: Context): RemainduDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    RemainduDatabase::class.java,
                    "remaindu.db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build().also { INSTANCE = it }
            }
    }
}
