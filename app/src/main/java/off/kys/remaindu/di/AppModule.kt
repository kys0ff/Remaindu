package off.kys.remaindu.di

import off.kys.remaindu.data.local.RemainduDatabase
import off.kys.remaindu.data.repository.NoticeRepositoryImpl
import off.kys.remaindu.domain.repository.NoticeRepository
import off.kys.remaindu.domain.usecase.AcknowledgeNoticeUseCase
import off.kys.remaindu.domain.usecase.AddNoticeUseCase
import off.kys.remaindu.domain.usecase.DeleteNoticeUseCase
import off.kys.remaindu.domain.usecase.GetNoticeUseCase
import off.kys.remaindu.domain.usecase.NoticeUseCases
import off.kys.remaindu.domain.usecase.ObserveDueNoticesUseCase
import off.kys.remaindu.domain.usecase.ObserveNoticesUseCase
import off.kys.remaindu.domain.usecase.UpdateNoticeUseCase
import off.kys.remaindu.presentation.notice.NoticeOverlayModel
import off.kys.remaindu.presentation.screen.create.CreateNoticeScreenModel
import off.kys.remaindu.presentation.screen.home.HomeScreenModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { RemainduDatabase.getInstance(androidContext()) }
    single { get<RemainduDatabase>().noticeDao() }
}

val repositoryModule = module {
    single<NoticeRepository> { NoticeRepositoryImpl(androidContext(), get()) }
}

val useCaseModule = module {
    factory { ObserveNoticesUseCase(get()) }
    factory { ObserveDueNoticesUseCase(get()) }
    factory { GetNoticeUseCase(get()) }
    factory { AddNoticeUseCase(get()) }
    factory { UpdateNoticeUseCase(get()) }
    factory { DeleteNoticeUseCase(get()) }
    factory { AcknowledgeNoticeUseCase(get()) }
    factory {
        NoticeUseCases(
            observeNotices = get(),
            observeDueNotices = get(),
            getNotice = get(),
            addNotice = get(),
            updateNotice = get(),
            deleteNotice = get(),
            acknowledgeNotice = get()
        )
    }
}

val screenModelModule = module {
    factory { HomeScreenModel(get()) }
    factory { (noticeId: Long?) -> CreateNoticeScreenModel(noticeId, get()) }
    factory { NoticeOverlayModel(get()) }
}

val appModules = listOf(databaseModule, repositoryModule, useCaseModule, screenModelModule)
