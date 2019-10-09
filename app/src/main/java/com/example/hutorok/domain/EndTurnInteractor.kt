package com.example.hutorok.domain

import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.IEndTasksListInteractor
import com.example.hutorok.domain.storage.IHutorStatusesListInteractor
import com.example.hutorok.domain.storage.IMessageInteractor
import com.example.hutorok.domain.storage.IWorkersListInteractor
import io.reactivex.Observable
import io.reactivex.functions.Function3

class EndTurnInteractor(
    private val workersListInteractor: IWorkersListInteractor,
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor,
    private val endTasksListInteractor: IEndTasksListInteractor,
    private val messageInteractor: IMessageInteractor
) : IEndTurnInteractor {

    override fun execute() {
        Observable.zip(
            workersListInteractor.get(),
            endTasksListInteractor.get(),
            hutorStatusesListInteractor.get(),
            Function3 { workers: List<Worker>, tasksList: List<Task>, statusesList: List<Status> ->
                var message = ""
                tasksList.forEach { task ->
                    val point = task.countPoint(workers, statusesList)

                    val newStatusesList = statusesList.toMutableList()
                    task.results.forEach { taskResult ->
                        taskResult.makeAction(newStatusesList, point, workers)
                        message = message + taskResult.describe.replace("N", point.toString()) + "\n"
                    }

                    hutorStatusesListInteractor.update(newStatusesList)
                }
                markWorkersAsRested(workers)
                message += eatFood(workers, statusesList)
                messageInteractor.update("Ход закончен. В результате: $message")
            }
        ).subscribe()
    }

    private fun markWorkersAsRested(workers: List<Worker>) {
        workers.forEach { worker ->
            val findStatus = worker.statuses.find { status -> status.code == "worked" }
            if (findStatus != null) {
                if (findStatus.value <= 1) {
                    val toMutableList = worker.statuses.toMutableList()
                    toMutableList.remove(findStatus)
                    worker.statuses = toMutableList
                } else {
                    findStatus.value = findStatus.value - 1
                }
            }
        }
    }

    private fun eatFood(workers: List<Worker>, statusesList: List<Status>): String {
        val findStatus = statusesList.find { status -> status.code == "foodsRES" }
        val size = workers.size
        if (findStatus != null) {
            findStatus.value = findStatus.value - size
        }
        return "Было съедено $size еды"
    }

}