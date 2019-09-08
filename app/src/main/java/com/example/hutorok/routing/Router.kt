package com.example.hutorok.routing

import com.example.hutorok.NowScreen
import com.example.hutorok.NowScreen.*
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class Router : IRouter {

    private val nowScreen = BehaviorSubject.create<NowScreen>()
    private val pressBackButtonEvents = PublishSubject.create<Unit>()

    override fun nowScreen(): Observable<NowScreen> {
        return Observable.combineLatest(
            nowScreen.startWith(START_SCREEN),
            pressBackButtonEvents.withLatestFrom(nowScreen,
                BiFunction { _: Unit, nowScreen: NowScreen ->
                    getPreviousScreen(nowScreen)
                })
                .doOnNext { nowScreen.onNext(it) }
                .map { Unit }
                .startWith(Unit),
            BiFunction { nowScreen: NowScreen, _ -> nowScreen })
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

    override fun routeToWorkersSccreen() {
        nowScreen.onNext(WORKERS_SCREEN)
    }

    override fun onBackPressed() {
        pressBackButtonEvents.onNext(Unit)
    }

    private fun getPreviousScreen(nowScreen: NowScreen): NowScreen {
        return when (nowScreen) {
            START_SCREEN -> CLOSE_SCREEN
            else -> START_SCREEN
        }
    }

}