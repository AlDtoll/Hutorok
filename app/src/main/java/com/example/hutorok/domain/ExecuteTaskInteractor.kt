package com.example.hutorok.domain

import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Task.Companion.deselectAll
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.*
import com.example.hutorok.routing.OnBackPressedInteractor
import com.example.hutorok.routing.RouteToFinishScreenInteractor
import io.reactivex.Observable
import io.reactivex.functions.Function3

class ExecuteTaskInteractor(
    private val workersListInteractor: IWorkersListInteractor,
    private val taskInteractor: ITaskInteractor,
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor,
    private val messageInteractor: IMessageInteractor,
    private val onBackPressedInteractor: OnBackPressedInteractor,
    private val invisibleStatusNamesListInteractor: IInvisibleStatusNamesListInteractor,
    private val routeToFinishScreenInteractor: RouteToFinishScreenInteractor
) : IExecuteTaskInteractor {

    companion object {
        const val EXECUTE_TASK_PREFIX = "Работа сделана. В результате: "
    }

    override fun execute(isQuest: Boolean) {
        var isFinished = false
        Observable.zip(
            workersListInteractor.get(),
            taskInteractor.get(),
            hutorStatusesListInteractor.get(),
            Function3 { workersList: MutableList<Worker>, task: Task, hutorStatusesList: MutableList<Status> ->
                val selectedWorkers = workersList.filter { it.isSelected }

                var message = ""
                task.results.forEach { taskResult ->
                    val point = taskResult.countPoint(selectedWorkers, hutorStatusesList)
                    message += taskResult.makeAction(hutorStatusesList, point, workersList)
                }

                if (hutorStatusesList.find { status -> status.code == "DEFEAT" || status.code == "VICTORY" } != null) {
                    isFinished = true
                    routeToFinishScreenInteractor.execute()
                }

                if (!isQuest && task.type != Task.Type.PERSON) {
                    message += makeFineForWorkers(selectedWorkers)
                    message += markWorkersAsWorked(selectedWorkers)
                }

                if (!isQuest) {
                    deselectAll(workersList)
                    messageInteractor.update(EXECUTE_TASK_PREFIX + message)
                } else {
                    messageInteractor.update(message)
                }

            }
        ).subscribe()
        invisibleStatusNamesListInteractor.refresh()
        if (!isQuest && !isFinished) {
            onBackPressedInteractor.execute()
        }
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