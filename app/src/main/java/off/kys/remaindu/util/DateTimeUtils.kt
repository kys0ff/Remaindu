package off.kys.remaindu.util

import off.kys.remaindu.domain.model.RepetitionType
import java.time.Instant
import java.time.ZoneId

object DateTimeUtils {

    /** Computes the next time a repeating notice should trigger, starting from [from]. */
    fun nextTrigger(from: Long, type: RepetitionType, customIntervalMinutes: Long?): Long {
        val zone = ZoneId.systemDefault()
        val zonedDateTime = Instant.ofEpochMilli(from).atZone(zone)
        return when (type) {
            RepetitionType.ONCE -> from
            RepetitionType.HOURLY -> zonedDateTime.plusHours(1).toInstant().toEpochMilli()
            RepetitionType.DAILY -> zonedDateTime.plusDays(1).toInstant().toEpochMilli()
            RepetitionType.WEEKLY -> zonedDateTime.plusWeeks(1).toInstant().toEpochMilli()
            RepetitionType.MONTHLY -> zonedDateTime.plusMonths(1).toInstant().toEpochMilli()
            RepetitionType.CUSTOM -> zonedDateTime.plusMinutes(
                (customIntervalMinutes ?: 60L).coerceAtLeast(1),
            ).toInstant().toEpochMilli()
        }
    }

    /** Small human-readable relative time, e.g. "in 2h", "Due now". */
    fun formatRelative(epochMillis: Long): String {
        val now = System.currentTimeMillis()
        val diff = epochMillis - now
        val minutes = diff / 60_000
        return when {
            diff <= 0 -> "Due now"
            minutes < 1 -> "in < 1m"
            minutes < 60 -> "in ${minutes}m"
            minutes < (60 * 24) -> "in ${minutes / 60}h"
            else -> "in ${minutes / (60 * 24)}d"
        }
    }
}
