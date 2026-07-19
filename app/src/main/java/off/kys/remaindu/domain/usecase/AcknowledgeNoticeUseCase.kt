package off.kys.remaindu.domain.usecase

import off.kys.remaindu.domain.repository.NoticeRepository

class AcknowledgeNoticeUseCase(private val repository: NoticeRepository) {
    suspend operator fun invoke(id: Long) = repository.acknowledge(id)
}
