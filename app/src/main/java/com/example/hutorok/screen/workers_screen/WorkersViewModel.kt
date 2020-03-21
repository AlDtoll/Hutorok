package com.example.hutorok.screen.workers_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.IExecuteTaskInteractor
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.*
import com.example.hutorok.routing.IScenarioInteractor
import com.example.hutorok.routing.RouteToWorkerInfoScreenInteractor
import com.example.hutorok.routing.Scenario
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class WorkersViewModel(
    private val workersListInteractor: IWorkersListInteractor,
    private val workerInteractor: IWorkerInteractor,
    private val routeToWorkerInfoScreenInteractor: RouteToWorkerInfoScreenInteractor,
    private val scenarioInteractor: IScenarioInteractor,
    private val executeTaskInteractor: IExecuteTaskInteractor,
    private val importantStatusNamesListInteractor: IImportantStatusNamesListInteractor,
    private val taskInteractor: ITaskInteractor,
    private val generalDisableStatusListInteractor: IGeneralDisableStatusListInteractor,
    private val questInteractor: IQuestInteractor
) : IWorkersViewModel {

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
        val observable = Observable.combineLatest(
            taskInteractor.get(),
            workersListInteractor.get(),
            BiFunction { task: Task, workers: List<Worker> ->
                val filteredWorkers = workers.filter { worker -> worker.isSelected }
                if (task.type == Task.Type.BUILD) {
                    return@BiFunction true
                }
                if (task.type == Task.Type.MASTER_SLAVE_JOB) {
                    return@BiFunction filteredWorkers.size == 2
                }
                if (task.type == Task.Type.PERSON || task.type == Task.Type.PERSONAL_JOB) {
                    return@BiFunction filteredWorkers.size == 1
                } else {
                    return@BiFunction workers.any { worker -> worker.isSelected }
                }

            }
        )
        return LiveDataReactiveStreams.fromPublisher(
            observable.toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun clickCheckbox(worker: Worker) {
        workersListInteractor.refresh()
    }

    override fun generalDisableStatus(): LiveData<List<Triple<String, Task.Symbol, Double>>> {
        return LiveDataReactiveStreams.fromPublisher(
            generalDisableStatusListInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )
    }

}