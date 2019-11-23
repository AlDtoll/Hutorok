package com.example.hutorok.domain

import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Task.Companion.deselectAll
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
            Function3 { workersList: MutableList<Worker>, task: Task, hutorStatusesList: MutableList<Status> ->
                val selectedWorkers = workersList.filter { it.isSelected }

                val point = task.countPoint(selectedWorkers, hutorStatusesList)

                var message = ""
                task.results.forEach { taskResult ->
                    taskResult.makeAction(hutorStatusesList, point, workersList)
                    message += taskResult.makeMessage(workersList)
                }

                if (task.type != Task.Type.PERSON) {
                    message += makeFineForWorkers(selectedWorkers)
                    message += markWorkersAsWorked(selectedWorkers)
                }
                deselectAll(workersList)

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

}