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
    private val questInteractor: IQuestInteractor,
    private val afterWorkTaskInteractor: IAfterWorkTaskInteractor
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
            workersListInteractor.get(),
            taskInteractor.get(),
            buildsListInteractor.get(),
            afterWorkTaskInteractor.get(),
            Function5 { _: Unit, workersList: MutableList<Worker>, task: Task, hutorStatusesList: MutableList<Status>, afterWorkTask: Task ->
                var message = ""
                val isQuest = questInteractor.value()
                task.results.forEach { taskResult ->
                    message += taskResult.makeAction(hutorStatusesList, workersList)
                }

                if (hutorStatusesList.find { status -> status.code == "DEFEAT" || status.code == "VICTORY" } != null) {
                    routeToFinishScreenInteractor.execute()
                }

                //todo selectedWorkers or workersList?
                val selectedWorkers = workersList.filter { it.isSelected }
                if (!isQuest && task.type != Task.Type.PERSON) {
                    afterWorkTask.results.forEach { taskResult ->
                        message += taskResult.makeAction(
                            hutorStatusesList,
                            selectedWorkers as MutableList<Worker>
                        )
                    }
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

}