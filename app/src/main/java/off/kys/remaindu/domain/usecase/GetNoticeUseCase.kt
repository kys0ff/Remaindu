package off.kys.remaindu.domain.usecase

import off.kys.remaindu.domain.model.Notice
import off.kys.remaindu.domain.repository.NoticeRepository

class GetNoticeUseCase(private val repository: NoticeRepository) {
    suspend operator fun invoke(id: Long): Notice? = repository.getById(id)
}
