package com.example.hutorok.screen.workers_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.IExecuteTaskInteractor
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.*
import com.example.hutorok.routing.IScenarioInteractor
import com.example.hutorok.routing.RouteToWorkerInfoScreenInteractor
import com.example.hutorok.routing.Scenario
import io.reactivex.BackpressureStrategy
import io.reactivex.functions.Function3
import io.reactivex.functions.Function4
import io.reactivex.subjects.PublishSubject

class WorkersViewModel(
    private val workersListInteractor: IWorkersListInteractor,
    private val workerInteractor: IWorkerInteractor,
    private val routeToWorkerInfoScreenInteractor: RouteToWorkerInfoScreenInteractor,
    private val scenarioInteractor: IScenarioInteractor,
    private val executeTaskInteractor: IExecuteTaskInteractor,
    private val importantStatusNamesListInteractor: IImportantStatusNamesListInteractor,
    private val taskInteractor: ITaskInteractor,
    private val generalDisableStatusListInteractor: IGeneralDisableStatusListInteractor,
    private val questInteractor: IQuestInteractor,
    private val buildsListInteractor: IBuildsListInteractor
) : IWorkersViewModel {

    private val event = PublishSubject.create<Unit>()

    override fun workersData(): LiveData<MutableList<Worker>> {
        val visibleWorkers = workersListInteractor.get()
            .map { workers -> workers.filter { worker -> !worker.isInvisible }.toMutableList() }
        return LiveDataReactiveStreams.fromPublisher(
            visibleWorkers.toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun clickWorker(worker: Worker) {
        workerInteractor.update(worker)
        routeToWorkerInfoScreenInteractor.execute()
    }

    override fun isOrderScenario(): LiveData<Boolean> {
        val observable = scenarioInteractor.get().map { it == Scenario.ORDER }
        return LiveDataReactiveStreams.fromPublisher(
            observable.toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun clickExecute() {
        questInteractor.update(false)
        executeTaskInteractor.execute()
        event.onNext(Unit)
    }

    override fun executeTaskDataResponse(): LiveData<Unit> =
        LiveDataReactiveStreams.fromPublisher(
            executeTaskInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )

    override fun importantStatusesData(): LiveData<List<String>> {
        return LiveDataReactiveStreams.fromPublisher(
            importantStatusNamesListInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun taskData(): LiveData<Task> =
        LiveDataReactiveStreams.fromPublisher(
            taskInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )

    override fun isExecuteTaskButtonEnable(): LiveData<Boolean> {
        val observable = event.withLatestFrom(
            taskInteractor.get(),
            workersListInteractor.get(),
            buildsListInteractor.get(),
            Function4 { _: Unit, task: Task, workers: List<Worker>, builds: List<Status> ->
                if (Task.allConditionsIsComplete(task.permissiveConditions, builds)) {
                    return@Function4 isSomeWorkerSelected(workers, task)
                } else {
                    return@Function4 false
                }
            }
        )
        return LiveDataReactiveStreams.fromPublisher(
            observable.toFlowable(BackpressureStrategy.BUFFER)
        )
    }

    override fun checkExecuteButton() {
        event.onNext(Unit)
    }

    override fun generalDisableStatus(): LiveData<List<Triple<String, Task.Symbol, Double>>> {
        return LiveDataReactiveStreams.fromPublisher(
            generalDisableStatusListInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun isExecuteButtonHintVisible(): LiveData<Boolean> {
        val observable = event.withLatestFrom(
            taskInteractor.get(),
            buildsListInteractor.get(),
            Function3 { _: Unit, task: Task, builds: List<Status> ->
                return@Function3 !Task.allConditionsIsComplete(task.permissiveConditions, builds)
            }
        )
        return LiveDataReactiveStreams.fromPublisher(
            observable.toFlowable(BackpressureStrategy.BUFFER)
        )
    }

    private fun isSomeWorkerSelected(
        workers: List<Worker>,
        task: Task
    ): Boolean {
        val filteredWorkers = workers.filter { worker -> worker.isSelected }
        if (task.type == Task.Type.BUILD) {
            return true
        }
        if (task.type == Task.Type.MASTER_SLAVE_JOB) {
            return filteredWorkers.size == 2
        }
        return if (task.type == Task.Type.PERSON || task.type == Task.Type.PERSONAL_JOB) {
            filteredWorkers.size == 1
        } else {
            workers.any { worker -> worker.isSelected }
        }
    }

}