package com.example.hutorok

import com.example.hutorok.routing.*
import com.example.hutorok.screen.IStartViewModel
import com.example.hutorok.screen.StartViewModel
import org.koin.dsl.module.module

val appModule = module {
    single { this }
    single { MainViewModel(get()) as IMainViewModel }

    single { Router() as IRouter }
    single { GetNowScreenInteractor(get()) as IGetNowScreenInteractor }
    single { RouteToStartScreenInteractor(get()) }
    single { RouteToWorkersScreenInteractor(get()) }
    single { RouteToBuildsScreenInteractor(get()) }
    single { RouteToTasksScreenInteractor(get()) }

    single { StartViewModel(get(), get(), get()) as IStartViewModel }
}