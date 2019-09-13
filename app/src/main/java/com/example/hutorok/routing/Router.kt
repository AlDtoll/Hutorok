package com.example.hutorok.routing

import com.example.hutorok.NowScreen
import com.example.hutorok.NowScreen.*
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class Router(
    private val scenarioInteractor: IScenarioInteractor
) : IRouter {

    private val nowScreen = BehaviorSubject.create<NowScreen>()
    private val pressBackButtonEvents = PublishSubject.create<Unit>()

    override fun nowScreen(): Observable<NowScreen> {
        val observable = Observable.combineLatest(nowScreen.startWith(START_SCREEN),
            scenarioInteractor.get().startWith(Scenario.SPEAK),
            BiFunction { nowScreen: NowScreen, scenario: Scenario ->
                nowScreen to scenario
            })
        return Observable.combineLatest(
            observable,
            pressBackButtonEvents.withLatestFrom(
                observable,
                BiFunction { _: Unit, pair: Pair<NowScreen, Scenario> ->
                    getPreviousScreen(pair.first, pair.second)
                })
                .doOnNext { nowScreen.onNext(it) }
                .map { Unit }
                .startWith(Unit),
            BiFunction { pair: Pair<NowScreen, Scenario>, _ -> pair.first })
    }

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

    override fun onBackPressed() {
        pressBackButtonEvents.onNext(Unit)
    }

    private fun getPreviousScreen(nowScreen: NowScreen, scenario: Scenario): NowScreen {
        return when (nowScreen) {
            START_SCREEN -> CLOSE_SCREEN
            WORKER_INFO_SCREEN -> WORKERS_SCREEN
            WORKERS_SCREEN -> getPreviousScreenForWorkersScreen(scenario)
            else -> START_SCREEN
        }
    }

    private fun getPreviousScreenForWorkersScreen(scenario: Scenario): NowScreen {
        return when (scenario) {
            Scenario.SPEAK -> START_SCREEN
            Scenario.ORDER -> TASKS_SCREEN
        }
    }

}