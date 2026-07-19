package off.kys.remaindu.domain.usecase

import off.kys.remaindu.domain.repository.NoticeRepository

class DeleteNoticeUseCase(private val repository: NoticeRepository) {
    suspend operator fun invoke(id: Long) = repository.delete(id)
}
