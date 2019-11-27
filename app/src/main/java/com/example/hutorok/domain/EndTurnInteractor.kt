package com.example.hutorok.domain

import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Task.Companion.deselectAll
import com.example.hutorok.domain.model.Task.Companion.selectAll
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.*
import io.reactivex.Observable
import io.reactivex.functions.Function3

class EndTurnInteractor(
    private val workersListInteractor: IWorkersListInteractor,
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor,
    private val endTasksListInteractor: IEndTasksListInteractor,
    private val messageInteractor: IMessageInteractor,
    private val turnNumberInteractor: ITurnNumberInteractor,
    private val invisibleStatusNamesListInteractor: IInvisibleStatusNamesListInteractor
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
                tasksList.forEach { task ->
                    selectAll(workers, task.enableConditions)
                    val point = task.countPoint(workers, hutorStatusesList)

                    task.results.forEach { taskResult ->
                        taskResult.makeAction(hutorStatusesList, point, workers)
                        message += taskResult.makeMessage(workers)
                    }
                    deselectAll(workers)
                }

                messageInteractor.update(END_TURN_PREFIX + message)
                invisibleStatusNamesListInteractor.refresh()
                turnNumberInteractor.increment()
            }
        ).subscribe()
    }

}