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

    override fun execute() {
        Observable.zip(
            workersListInteractor.get(),
            taskInteractor.get(),
            hutorStatusesListInteractor.get(),
            Function3 { workersList: List<Worker>, task: Task, hutorStatusesList: List<Status> ->
                val workers = workersList.filter { it.isChecked }

                val point = task.countPoint(workers, hutorStatusesList)

                var message = ""
                val hutorStatuses = hutorStatusesList.toMutableList()
                task.results.forEach { taskResult ->
                    taskResult.makeAction(hutorStatuses, point, workers)
                    message += taskResult.makeMessage(point)
                }

                makeFineForWorkers(workers)
                if (task.type != Task.Type.PERSON) {
                    message += markWorkersAsWorked(workers)
                }
                letWorkersGo(workers)

                when (task.type) {
                    Task.Type.WORK -> hutorStatusesListInteractor.update(hutorStatuses)
                    Task.Type.BUILD -> hutorStatusesListInteractor.update(hutorStatuses)
                    Task.Type.PERSON -> Unit
                }
                messageInteractor.update("Работа сделана. В результате: $message")
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

    private fun markWorkersAsWorked(workers: List<Worker>) {
        workers.forEach { worker ->
            worker.markAsWorked()
        }
    }

    private fun letWorkersGo(workers: List<Worker>) {
        workers.forEach { worker -> worker.isChecked = false }
    }

}