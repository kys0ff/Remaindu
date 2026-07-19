package off.kys.remaindu.domain.usecase

import off.kys.remaindu.domain.model.Notice
import off.kys.remaindu.domain.repository.NoticeRepository

class AddNoticeUseCase(private val repository: NoticeRepository) {
    suspend operator fun invoke(notice: Notice): Long = repository.add(notice)
}
