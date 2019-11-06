package com.example.hutorok.domain

import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.IHutorStatusesListInteractor
import com.example.hutorok.domain.storage.IMessageInteractor
import com.example.hutorok.domain.storage.ITaskInteractor
import com.example.hutorok.domain.storage.IWorkersListInteractor
import com.example.hutorok.routing.OnBackPressedInteractor
import io.reactivex.Observable
import io.reactivex.functions.Function3

class ExecuteTaskInteractor(
    private val workersListInteractor: IWorkersListInteractor,
    private val taskInteractor: ITaskInteractor,
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor,
    private val messageInteractor: IMessageInteractor,
    private val onBackPressedInteractor: OnBackPressedInteractor
) : IExecuteTaskInteractor {

    companion object {
        const val EXECUTE_TASK_PREFIX = "Работа сделана. В результате: "
    }

    override fun execute() {
        Observable.zip(
            workersListInteractor.get(),
            taskInteractor.get(),
            hutorStatusesListInteractor.get(),
            Function3 { workersList: List<Worker>, task: Task, hutorStatusesList: List<Status> ->
                val selectedWorkers = workersList.filter { it.isSelected }

                val point = task.countPoint(selectedWorkers, hutorStatusesList)

                var message = ""
                val hutorStatuses = hutorStatusesList.toMutableList()
                task.results.forEach { taskResult ->
                    taskResult.makeAction(hutorStatuses, point, selectedWorkers)
                    message += taskResult.makeMessage(point, selectedWorkers)
                }

                if (task.type != Task.Type.PERSON) {
                    message += makeFineForWorkers(selectedWorkers)
                    message += markWorkersAsWorked(selectedWorkers)
                }
                letWorkersGo(selectedWorkers)

                hutorStatusesListInteractor.update(hutorStatuses)
                messageInteractor.update(EXECUTE_TASK_PREFIX + message)
            }
        ).subscribe()
        onBackPressedInteractor.execute()
    }

    private fun makeFineForWorkers(workers: List<Worker>): String {
        var message = ""
        workers.forEach { worker ->
            message += worker.fine()
        }
        return message
    }

    private fun markWorkersAsWorked(workers: List<Worker>): String {
        var message = ""
        workers.forEach { worker ->
            message += worker.markAsWorked()
        }
        return message
    }

    private fun letWorkersGo(workers: List<Worker>) {
        workers.forEach { worker -> worker.isSelected = false }
    }

}