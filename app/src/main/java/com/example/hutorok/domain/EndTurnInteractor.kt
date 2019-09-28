package com.example.hutorok.domain

import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.TaskAction
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.IEndTasksListInteractor
import com.example.hutorok.domain.storage.IHutorStatusesListInteractor
import com.example.hutorok.domain.storage.IMessageInteractor
import com.example.hutorok.domain.storage.IWorkersListInteractor
import io.reactivex.Observable
import io.reactivex.functions.Function3
import java.util.*
import kotlin.random.Random

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
                    val point = countTaskPoint(workers, task, statusesList)

                    val newStatusesList = statusesList.toMutableList()
                    task.results.forEach { taskResult ->
                        if (taskResult.action == TaskAction.ADD_STATUS) {
                            newStatusesList.add(taskResult.status)
                        } else {
                            val findStatus = newStatusesList.find { status -> taskResult.status.code == status.code }
                            if (findStatus == null) {
                                //TODO сделать конструктор копирования - тут изменяется значение статуса
                                taskResult.status.value = when (taskResult.action) {
                                    TaskAction.CHANGE_STATUS_VALUE -> taskResult.status.value + point
                                    TaskAction.SET_STATUS_VALUE -> taskResult.status.value
                                    TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT -> taskResult.status.value
                                    TaskAction.ADD_STATUS -> 0.0
                                }
                                newStatusesList.add(taskResult.status)
                            } else {
                                findStatus.value = when (taskResult.action) {
                                    TaskAction.CHANGE_STATUS_VALUE -> findStatus.value + point
                                    TaskAction.SET_STATUS_VALUE -> taskResult.status.value
                                    TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT -> findStatus.value + taskResult.status.value
                                    TaskAction.ADD_STATUS -> 0.0
                                }
                            }
                        }
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

    private fun countTaskPoint(
        workers: List<Worker>,
        task: Task,
        statusesList: List<Status>
    ): Double {
        var usualWorkerPoint = 0
        workers.forEach { _ ->
            usualWorkerPoint += Random(Date().time).nextInt(task.workerFunction.defaultValue) + 1
        }
        var specialWorkerPoint = 0.0
        task.workerFunction.statuses.forEach { pair ->
            workers.forEach { worker ->
                worker.statuses.forEach {
                    if (it.code == pair.first) {
                        specialWorkerPoint += pair.second
                    }
                }
            }
        }
        val usualHutorPoint = task.hutorFunction.defaultValue
        var specialHutorPoint = 0.0
        task.hutorFunction.statuses.forEach { pair ->
            statusesList.forEach {
                if (it.code == pair.first) {
                    specialHutorPoint += pair.second
                }
            }
        }
        return usualWorkerPoint + specialWorkerPoint + usualHutorPoint + specialHutorPoint
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