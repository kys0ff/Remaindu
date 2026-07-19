package off.kys.remaindu.domain.usecase

import kotlinx.coroutines.flow.Flow
import off.kys.remaindu.domain.model.Notice
import off.kys.remaindu.domain.repository.NoticeRepository

class ObserveDueNoticesUseCase(private val repository: NoticeRepository) {
    operator fun invoke(): Flow<List<Notice>> = repository.observeDue()
}
