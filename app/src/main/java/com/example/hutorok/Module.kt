package com.example.hutorok

import com.example.hutorok.domain.storage.IWorkersListInteractor
import com.example.hutorok.domain.storage.WorkersListInteractor
import com.example.hutorok.routing.*
import com.example.hutorok.screen.start.IStartViewModel
import com.example.hutorok.screen.start.StartViewModel
import com.example.hutorok.screen.workers.IWorkersViewModel
import com.example.hutorok.screen.workers.WorkersViewModel
import org.koin.dsl.module.module

val appModule = module {
    single { this }
    single { MainViewModel(get(), get()) as IMainViewModel }

    single { Router() as IRouter }
    single { GetNowScreenInteractor(get()) as IGetNowScreenInteractor }
    single { RouteToStartScreenInteractor(get()) }
    single { RouteToWorkersScreenInteractor(get()) }
    single { RouteToBuildsScreenInteractor(get()) }
    single { RouteToTasksScreenInteractor(get()) }
    single { OnBackPressedInteractor(get()) }

    single { StartViewModel(get(), get(), get()) as IStartViewModel }
    single { WorkersViewModel() as IWorkersViewModel }

    single { WorkersListInteractor() as IWorkersListInteractor }
}