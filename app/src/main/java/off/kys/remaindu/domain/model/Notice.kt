package off.kys.remaindu.domain.model

/**
 * A thing the user wants to remember, shown on screen when due and
 * hidden again once acknowledged (tapped / marked as read).
 */
data class Notice(
    val id: Long = 0,
    val title: String,
    val message: String,
    val repetitionType: RepetitionType = RepetitionType.ONCE,
    val customIntervalMinutes: Long? = null,
    val createdAt: Long,
    val nextTriggerAt: Long,
    val lastAcknowledgedAt: Long? = null,
    val isActive: Boolean = true
) {
    val isDue: Boolean
        get() = isActive && nextTriggerAt <= System.currentTimeMillis()
}
