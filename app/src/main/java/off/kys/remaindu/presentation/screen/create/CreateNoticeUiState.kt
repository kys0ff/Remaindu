package off.kys.remaindu.presentation.screen.create

import off.kys.remaindu.domain.model.RepetitionType

data class CreateNoticeUiState(
    val id: Long = 0,
    val title: String = "",
    val message: String = "",
    val repetitionType: RepetitionType = RepetitionType.ONCE,
    val customIntervalMinutes: String = "60",
    val isEditing: Boolean = false,
    val createdAt: Long = 0
) {
    val isValid: Boolean get() = title.isNotBlank()
}