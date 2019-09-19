package com.example.hutorok.domain

import com.example.hutorok.domain.model.*
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

    override fun execute() {
        Observable.zip(
            workersListInteractor.get(),
            taskInteractor.get(),
            hutorStatusesListInteractor.get(),
            Function3 { workersList: List<Worker>, task: Task, statusesList: List<Status> ->
                val workers = workersList.filter { it.isChecked }
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
                val point = usualWorkerPoint + specialWorkerPoint + usualHutorPoint + specialHutorPoint
                var message = ""
                val newStatusesList = statusesList.toMutableList()
                task.results.forEach { taskResult ->
                    when (taskResult.target) {
                        TaskTarget.HUTOR -> {
                            var isStatusChanged = false
                            newStatusesList.forEach { status ->
                                if (status.code == taskResult.status.code) {
                                    isStatusChanged = true
                                    when (taskResult.action) {
                                        TaskAction.CHANGE_STATUS_VALUE -> status.value = status.value + point
                                        TaskAction.SET_STATUS_VALUE -> status.value = taskResult.status.value
                                    }
                                }
                            }
                            if (!isStatusChanged) {
                                taskResult.status.value = point
                                newStatusesList.add(taskResult.status)
                            }
                            message = message + taskResult.describe.replace("N", point.toString()) + "\n"
                        }
                    }
                }
                workers.forEach { worker ->
                    var isStatusChanged = false
                    worker.statuses.forEach { status ->
                        if (status.code == "worked") {
                            isStatusChanged = true
                            status.value = status.value + 1
                        }
                    }
                    if (!isStatusChanged) {
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
                    }
                }
                hutorStatusesListInteractor.update(newStatusesList)
                messageInteractor.update("Работа сделана. В результате: $message")
            }
        ).subscribe()
    }

}