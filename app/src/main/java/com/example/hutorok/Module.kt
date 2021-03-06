package com.example.hutorok

import com.example.hutorok.domain.*
import com.example.hutorok.domain.storage.*
import com.example.hutorok.network.ApiProvider
import com.example.hutorok.routing.*
import com.example.hutorok.screen.builds_screen.BuildsViewModel
import com.example.hutorok.screen.builds_screen.IBuildsViewModel
import com.example.hutorok.screen.finish_screen.FinishViewModel
import com.example.hutorok.screen.finish_screen.IFinishViewModel
import com.example.hutorok.screen.history_screen.HistoryViewModel
import com.example.hutorok.screen.history_screen.IHistoryViewModel
import com.example.hutorok.screen.quest_screen.IQuestViewModel
import com.example.hutorok.screen.quest_screen.QuestViewModel
import com.example.hutorok.screen.start_screen.IStartViewModel
import com.example.hutorok.screen.start_screen.StartViewModel
import com.example.hutorok.screen.tasks_screen.ITasksViewModel
import com.example.hutorok.screen.tasks_screen.TasksViewModel
import com.example.hutorok.screen.worker_info_screen.IWorkerInfoViewModel
import com.example.hutorok.screen.worker_info_screen.WorkerInfoViewModel
import com.example.hutorok.screen.workers_screen.IWorkersViewModel
import com.example.hutorok.screen.workers_screen.WorkersViewModel
import org.koin.dsl.module.module

val appModule = module {
    single { this }
    single {
        MainViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        ) as IMainViewModel
    }

    single { Router(get()) as IRouter }
    single { GetNowScreenInteractor(get()) as IGetNowScreenInteractor }
    single { RouteToStartScreenInteractor(get()) }
    single { RouteToWorkersScreenInteractor(get(), get()) }
    single { RouteToBuildsScreenInteractor(get()) }
    single { RouteToTasksScreenInteractor(get()) }
    single { RouteToWorkerInfoScreenInteractor(get()) }
    single { RouteToTaskInfoInteractor(get(), get()) }
    single { RouteToHistoryScreenInteractor(get()) }
    single { RouteToQuestScreenInteractor(get()) }
    single { RouteToFinishScreenInteractor(get()) }
    single { OnBackPressedInteractor(get()) }

    single {
        LoadDataInteractor(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        ) as ILoadDataInteractor
    }
    single { ScenarioInteractor() as IScenarioInteractor }
    single {
        ExecuteTaskInteractor(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        ) as IExecuteTaskInteractor
    }
    single {
        EndTurnInteractor(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        ) as IEndTurnInteractor
    }

    single { StartViewModel(get(), get(), get(), get()) as IStartViewModel }
    single {
        WorkersViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        ) as IWorkersViewModel
    }
    single { WorkerInfoViewModel(get(), get()) as IWorkerInfoViewModel }
    single { TasksViewModel(get(), get(), get(), get(), get(), get()) as ITasksViewModel }
    single { BuildsViewModel(get()) as IBuildsViewModel }
    single { HistoryViewModel(get()) as IHistoryViewModel }
    single {
        QuestViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        ) as IQuestViewModel
    }
    single { FinishViewModel(get(), get()) as IFinishViewModel }

    single { WorkersListInteractor(get()) as IWorkersListInteractor }
    single { WorkerInteractor() as IWorkerInteractor }
    single { TasksListInteractor() as ITasksListInteractor }
    single { TaskInteractor() as ITaskInteractor }
    single { BuildsListInteractor() as IBuildsListInteractor }
    single { MessageInteractor(get()) as IMessageInteractor }
    single { ImportantStatusNamesListInteractor() as IImportantStatusNamesListInteractor }
    single { EndTasksListInteractor() as IEndTasksListInteractor }
    single { HistoryInteractor() as IHistoryInteractor }
    single { TurnNumberInteractor(get()) as ITurnNumberInteractor }
    single { InvisibleStatusNamesListInteractor(get()) as IInvisibleStatusNamesListInteractor }
    single { StartQuestInteractor() as IStartQuestInteractor }
    single { AdventuresListInteractor() as IAdventuresListInteractor }
    single { GeneralDisableStatusListInteractor() as IGeneralDisableStatusListInteractor }
    single { QuestInteractor() as IQuestInteractor }
    single { AfterWorkTaskInteractor() as IAfterWorkTaskInteractor }

    single { NavigationElementsVisibilityInteractor(get()) as INavigationElementsVisibilityInteractor }

    single { ApiProvider() }
    single { ApiProvider.get() }
}