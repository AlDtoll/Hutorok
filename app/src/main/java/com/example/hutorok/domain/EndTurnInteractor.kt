package com.example.hutorok.domain

import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Task.Companion.deselectAll
import com.example.hutorok.domain.model.Task.Companion.selectAll
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.*
import com.example.hutorok.routing.RouteToFinishScreenInteractor
import io.reactivex.Observable
import io.reactivex.functions.Function4
import io.reactivex.subjects.PublishSubject

class EndTurnInteractor(
    private val workersListInteractor: IWorkersListInteractor,
    private val buildsListInteractor: IBuildsListInteractor,
    private val endTasksListInteractor: IEndTasksListInteractor,
    private val messageInteractor: IMessageInteractor,
    private val turnNumberInteractor: ITurnNumberInteractor,
    private val invisibleStatusNamesListInteractor: IInvisibleStatusNamesListInteractor,
    private val routeToFinishScreenInteractor: RouteToFinishScreenInteractor,
    private val loadDataInteractor: ILoadDataInteractor
) : IEndTurnInteractor {

    companion object {
        const val END_TURN_PREFIX = "Ход закончен. В результате: "
    }

    private val event = PublishSubject.create<Unit>()

    override fun execute() {
        event.onNext(Unit)
    }

    override fun get(): Observable<Unit> {
        val observable = event.withLatestFrom(
            workersListInteractor.get(),
            endTasksListInteractor.get(),
            buildsListInteractor.get(),
            Function4 { _: Unit, workers: MutableList<Worker>, tasksList: List<Task>, hutorStatusesList: MutableList<Status> ->
                var message = ""
                tasksList
                    .forEach { task ->
                        if (Task.allConditionsIsComplete(
                                task.permissiveConditions,
                                hutorStatusesList
                            )
                        ) {
                            selectAll(workers, task.enableConditions)
                            task.results.forEach { taskResult ->
                                message += taskResult.makeAction(hutorStatusesList, workers)
                            }

                            if (hutorStatusesList.find { status -> status.code == "DEFEAT" || status.code == "VICTORY" } != null) {
                                routeToFinishScreenInteractor.execute()
                            }

                            deselectAll(workers)
                        }
                    }

                messageInteractor.update(END_TURN_PREFIX + message)
                invisibleStatusNamesListInteractor.refresh()
                turnNumberInteractor.increment()
                loadDataInteractor.saveResult()
            }
        )
        return observable
    }

}