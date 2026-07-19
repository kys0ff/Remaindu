package off.kys.remaindu.data.local

import androidx.room.TypeConverter
import off.kys.remaindu.domain.model.RepetitionType

class Converters {
    @TypeConverter
    fun fromRepetitionType(value: RepetitionType): String = value.name

    @TypeConverter
    fun toRepetitionType(value: String): RepetitionType = RepetitionType.valueOf(value)
}
