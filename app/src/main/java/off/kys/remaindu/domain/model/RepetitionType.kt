package off.kys.remaindu.domain.model

/**
 * How a [Notice] repeats once the user has acknowledged it.
 * ONCE never comes back; every other value re-schedules nextTriggerAt.
 */
enum class RepetitionType(val label: String) {
    ONCE("Once"),
    HOURLY("Every hour"),
    DAILY("Every day"),
    WEEKLY("Every week"),
    MONTHLY("Every month"),
    CUSTOM("Custom")
}
