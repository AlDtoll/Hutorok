package com.example.hutorok.domain

import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.TaskTarget
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
                task.results.forEach {
                    when (it.target) {
                        TaskTarget.HUTOR -> {
                            val newStatusesList = statusesList.toMutableList()
                            if (it.status.value == 0.0) {
                                it.status.value = point
                            }
                            newStatusesList.add(it.status)
                            hutorStatusesListInteractor.update(newStatusesList)
                            message = message + it.describe.replace("N", point.toString()) + "\n"
                        }
                    }
                }
                messageInteractor.update("Работа сделана в результате: $message")
            }
        ).subscribe()
    }

}