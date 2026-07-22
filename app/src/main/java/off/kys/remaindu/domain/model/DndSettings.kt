package off.kys.remaindu.domain.model

data class DndSettings(
    val isEnabled: Boolean = false,
    val endTime: Long? = null
) {
    val isCurrentlyActive: Boolean
        get() = isEnabled && (endTime == null || endTime > System.currentTimeMillis())
}
