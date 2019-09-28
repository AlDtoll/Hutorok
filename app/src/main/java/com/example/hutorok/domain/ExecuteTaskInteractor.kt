package com.example.hutorok.domain

import android.util.Log
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.TaskAction
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.IHutorStatusesListInteractor
import com.example.hutorok.domain.storage.IMessageInteractor
import com.example.hutorok.domain.storage.ITaskInteractor
import com.example.hutorok.domain.storage.IWorkersListInteractor
import io.reactivex.Observable
import io.reactivex.functions.Function3
import java.util.*
import kotlin.random.Random

class ExecuteTaskInteractor(
    private val workersListInteractor: IWorkersListInteractor,
    private val taskInteractor: ITaskInteractor,
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor,
    private val messageInteractor: IMessageInteractor
) : IExecuteTaskInteractor {

    private val TAG = "myLogs"

    override fun execute() {
        Observable.zip(
            workersListInteractor.get(),
            taskInteractor.get(),
            hutorStatusesListInteractor.get(),
            Function3 { workersList: List<Worker>, task: Task, statusesList: List<Status> ->
                Log.d(TAG, "1")
                val workers = workersList.filter { it.isChecked }

                val point = countTaskPoint(workers, task, statusesList)


                var message = ""
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

                markWorkersAsWorked(workers)

                hutorStatusesListInteractor.update(newStatusesList)
                messageInteractor.update("Работа сделана. В результате: $message")
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

    private fun markWorkersAsWorked(workers: List<Worker>) {
        workers.forEach { worker ->
            val findStatus = worker.statuses.find { status -> status.code == "worked" }
            if (findStatus == null) {
                val workerStatuses = worker.statuses.toMutableList()
                workerStatuses.add(
                    Status(
                        "worked",
                        "Работал",
                        "Если работать слишком много, то можно надорваться",
                        1.0,
                        true
                    )
                )
                worker.statuses = workerStatuses
            } else {
                findStatus.value = findStatus.value + 1
            }
        }
    }

}