package com.example.hutorok.domain

import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.*
import io.reactivex.Observable
import io.reactivex.functions.Function3

class EndTurnInteractor(
    private val workersListInteractor: IWorkersListInteractor,
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor,
    private val endTasksListInteractor: IEndTasksListInteractor,
    private val messageInteractor: IMessageInteractor,
    private val turnNumberInteractor: ITurnNumberInteractor
) : IEndTurnInteractor {

    companion object {
        const val END_TURN_PREFIX = "Ход закончен. В результате: "
    }

    override fun execute() {
        Observable.zip(
            workersListInteractor.get(),
            endTasksListInteractor.get(),
            hutorStatusesListInteractor.get(),
            Function3 { workers: MutableList<Worker>, tasksList: List<Task>, statusesList: List<Status> ->
                var message = ""
                tasksList.forEach { task ->
                    val point = task.countPoint(workers, statusesList)

                    val newStatusesList = statusesList.toMutableList()
                    task.results.forEach { taskResult ->
                        taskResult.makeAction(newStatusesList, point, workers)
                        message += taskResult.makeMessage(workers)
                    }

                    hutorStatusesListInteractor.update(newStatusesList)
                }
                message += markWorkersAsRested(workers)
                message += eatFood(workers, statusesList)
                messageInteractor.update(END_TURN_PREFIX + message)
                turnNumberInteractor.increment()
            }
        ).subscribe()
    }

    private fun markWorkersAsRested(workers: List<Worker>): String {
        var message = ""
        workers.forEach { worker ->
            message += worker.rest()
        }
        return message
    }

    private fun eatFood(workers: List<Worker>, statusesList: List<Status>): String {
        //todo сделать разное количество еды - возможно правило
        val findStatus = statusesList.find { status -> status.code == "foodsRES" }
        val size = workers.size
        if (findStatus != null) {
            findStatus.value = findStatus.value - size
        }
        return "Было съедено $size еды"
    }

}