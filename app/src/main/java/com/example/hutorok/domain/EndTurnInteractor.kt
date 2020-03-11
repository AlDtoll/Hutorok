package com.example.hutorok.domain

import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Task.Companion.deselectAll
import com.example.hutorok.domain.model.Task.Companion.selectAll
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.*
import com.example.hutorok.routing.RouteToFinishScreenInteractor
import io.reactivex.Observable
import io.reactivex.functions.Function3

class EndTurnInteractor(
    private val workersListInteractor: IWorkersListInteractor,
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor,
    private val endTasksListInteractor: IEndTasksListInteractor,
    private val messageInteractor: IMessageInteractor,
    private val turnNumberInteractor: ITurnNumberInteractor,
    private val invisibleStatusNamesListInteractor: IInvisibleStatusNamesListInteractor,
    private val routeToFinishScreenInteractor: RouteToFinishScreenInteractor
) : IEndTurnInteractor {

    companion object {
        const val END_TURN_PREFIX = "Ход закончен. В результате: "
    }

    override fun execute() {
        Observable.zip(
            workersListInteractor.get(),
            endTasksListInteractor.get(),
            hutorStatusesListInteractor.get(),
            Function3 { workers: MutableList<Worker>, tasksList: List<Task>, hutorStatusesList: MutableList<Status> ->
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
            }
        ).subscribe()
    }

}