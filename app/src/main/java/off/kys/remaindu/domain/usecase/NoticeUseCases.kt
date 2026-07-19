package off.kys.remaindu.domain.usecase

/** Bundles every notice-related use case so screen models take one dependency. */
data class NoticeUseCases(
    val observeNotices: ObserveNoticesUseCase,
    val observeDueNotices: ObserveDueNoticesUseCase,
    val getNotice: GetNoticeUseCase,
    val addNotice: AddNoticeUseCase,
    val updateNotice: UpdateNoticeUseCase,
    val deleteNotice: DeleteNoticeUseCase,
    val acknowledgeNotice: AcknowledgeNoticeUseCase
)
