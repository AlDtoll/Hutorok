package com.example.hutorok.routing

import com.example.hutorok.NowScreen
import com.example.hutorok.NowScreen.*
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class Router(
    private val scenarioInteractor: IScenarioInteractor
) : IRouter {

    private val nowScreen = BehaviorSubject.create<NowScreen>()
    private val pressBackButtonEvents = PublishSubject.create<Unit>()

    override fun nowScreen(): Observable<NowScreen> = nowScreen.startWith(TASKS_SCREEN)

    override fun routeToStartScreen() {
        nowScreen.onNext(START_SCREEN)
    }

    override fun routeToBuildsScreen() {
        nowScreen.onNext(BUILDS_SCREEN)
    }

    override fun routeToTasksScreen() {
        nowScreen.onNext(TASKS_SCREEN)
    }

    override fun routeToWorkersScreen() {
        nowScreen.onNext(WORKERS_SCREEN)
    }

    override fun routeToWorkerInfoScreen() {
        nowScreen.onNext(WORKER_INFO_SCREEN)
    }

    override fun routeToHistoryScreen() {
        nowScreen.onNext(HISTORY_SCREEN)
    }

    override fun routeToQuestScreen() {
        nowScreen.onNext(QUEST_SCREEN)
    }

    override fun routeToFinishScreen() {
        nowScreen.onNext(FINISH_SCREEN)
    }

    override fun onBackPressed() {
        pressBackButtonEvents.onNext(Unit)
        val previousScreen =
            getPreviousScreen(nowScreen.value ?: TASKS_SCREEN, scenarioInteractor.value())
        nowScreen.onNext(previousScreen)
    }

    private fun getPreviousScreen(nowScreen: NowScreen, scenario: Scenario): NowScreen {
        return when (nowScreen) {
            WORKER_INFO_SCREEN -> WORKERS_SCREEN
            WORKERS_SCREEN -> getPreviousScreenForWorkersScreen(scenario)
            else -> CLOSE_SCREEN
        }
    }

    private fun getPreviousScreenForWorkersScreen(scenario: Scenario): NowScreen {
        return when (scenario) {
            Scenario.SPEAK -> CLOSE_SCREEN
            Scenario.ORDER -> TASKS_SCREEN
        }
    }

}