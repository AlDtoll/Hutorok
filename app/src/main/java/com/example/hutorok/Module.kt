package com.example.hutorok

import com.example.hutorok.domain.ExecuteTaskInteractor
import com.example.hutorok.domain.IExecuteTaskInteractor
import com.example.hutorok.domain.ILoadDataInteractor
import com.example.hutorok.domain.MockLoadDataInteractor
import com.example.hutorok.domain.storage.*
import com.example.hutorok.routing.*
import com.example.hutorok.screen.builds_screen.BuildsViewModel
import com.example.hutorok.screen.builds_screen.IBuildsViewModel
import com.example.hutorok.screen.start.IStartViewModel
import com.example.hutorok.screen.start.StartViewModel
import com.example.hutorok.screen.tasks_screen.ITasksViewModel
import com.example.hutorok.screen.tasks_screen.TasksViewModel
import com.example.hutorok.screen.worker_info.IWorkerInfoViewModel
import com.example.hutorok.screen.worker_info.WorkerInfoViewModel
import com.example.hutorok.screen.workers.IWorkersViewModel
import com.example.hutorok.screen.workers.WorkersViewModel
import org.koin.dsl.module.module

val appModule = module {
    single { this }
    single { MainViewModel(get(), get(), get(), get()) as IMainViewModel }

    single { Router() as IRouter }
    single { GetNowScreenInteractor(get()) as IGetNowScreenInteractor }
    single { RouteToStartScreenInteractor(get()) }
    single { RouteToWorkersScreenInteractor(get(), get()) }
    single { RouteToBuildsScreenInteractor(get()) }
    single { RouteToTasksScreenInteractor(get()) }
    single { RouteToWorkerInfoScreenInteractor(get()) }
    single { RouteToTaskInfoInteractor(get(), get()) }
    single { OnBackPressedInteractor(get()) }

    single { MockLoadDataInteractor(get(), get(), get()) as ILoadDataInteractor }
    single { ScenarioInteractor() as IScenarioInteractor }
    single { ExecuteTaskInteractor(get(), get(), get(), get()) as IExecuteTaskInteractor }

    single { StartViewModel(get(), get(), get()) as IStartViewModel }
    single { WorkersViewModel(get(), get(), get(), get(), get()) as IWorkersViewModel }
    single { WorkerInfoViewModel(get()) as IWorkerInfoViewModel }
    single { TasksViewModel(get(), get(), get()) as ITasksViewModel }
    single { BuildsViewModel(get()) as IBuildsViewModel }

    single { WorkersListInteractor() as IWorkersListInteractor }
    single { WorkerInteractor() as IWorkerInteractor }
    single { TasksListInteractor() as ITasksListInteractor }
    single { TaskInteractor() as ITaskInteractor }
    single { HutorStatusesListInteractor() as IHutorStatusesListInteractor }
    single { MessageInteractor() as IMessageInteractor }
}