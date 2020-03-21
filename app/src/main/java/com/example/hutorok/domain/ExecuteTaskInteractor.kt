package com.example.hutorok.domain

import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Task.Companion.deselectAll
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.*
import com.example.hutorok.routing.RouteToFinishScreenInteractor
import io.reactivex.Observable
import io.reactivex.functions.Function5
import io.reactivex.subjects.PublishSubject

class ExecuteTaskInteractor(
    private val workersListInteractor: IWorkersListInteractor,
    private val taskInteractor: ITaskInteractor,
    private val buildsListInteractor: IBuildsListInteractor,
    private val messageInteractor: IMessageInteractor,
    private val invisibleStatusNamesListInteractor: IInvisibleStatusNamesListInteractor,
    private val routeToFinishScreenInteractor: RouteToFinishScreenInteractor,
    private val questInteractor: IQuestInteractor
) : IExecuteTaskInteractor {

    companion object {
        const val EXECUTE_TASK_PREFIX = "Работа сделана. В результате: "
    }

    private val event = PublishSubject.create<Unit>()

    override fun execute() {
        event.onNext(Unit)
    }

    override fun get(): Observable<Unit> {
        val observable = event.withLatestFrom(
            questInteractor.get(),
            workersListInteractor.get(),
            taskInteractor.get(),
            buildsListInteractor.get(),
            Function5 { _: Unit, isQuest: Boolean, workersList: MutableList<Worker>, task: Task, hutorStatusesList: MutableList<Status> ->
                val selectedWorkers = workersList.filter { it.isSelected }

                var message = ""
                task.results.forEach { taskResult ->
                    message += taskResult.makeAction(hutorStatusesList, workersList)
                }

                if (hutorStatusesList.find { status -> status.code == "DEFEAT" || status.code == "VICTORY" } != null) {
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
                //todo сомнительное место
                invisibleStatusNamesListInteractor.refresh()
            }
        )
        return observable
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