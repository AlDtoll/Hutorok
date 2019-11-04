package com.example.hutorok.screen.workers_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.IExecuteTaskInteractor
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.IImportantStatusNamesListInteractor
import com.example.hutorok.domain.storage.ITaskInteractor
import com.example.hutorok.domain.storage.IWorkerInteractor
import com.example.hutorok.domain.storage.IWorkersListInteractor
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
    private val taskInteractor: ITaskInteractor
) : IWorkersViewModel {

    override fun workersData(): LiveData<List<Worker>> =
        LiveDataReactiveStreams.fromPublisher(
            workersListInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )

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
        executeTaskInteractor.execute()
    }

    override fun importantStatusesData(): LiveData<List<String>> {
        return LiveDataReactiveStreams.fromPublisher(
            importantStatusNamesListInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun taskData(): LiveData<Task> =
        LiveDataReactiveStreams.fromPublisher(
            taskInteractor.get().map { it }.toFlowable(BackpressureStrategy.LATEST)
        )

    override fun isExecuteTaskButtonEnable(): LiveData<Boolean> {
        val observable = Observable.combineLatest(
            taskInteractor.get(),
            workersListInteractor.get(),
            BiFunction { task: Task, workers: List<Worker> ->
                if (task.type == Task.Type.BUILD) {
                    return@BiFunction true
                }
                if (task.type == Task.Type.PERSON) {
                    val filteredWorkers = workers.filter { worker -> worker.isChecked }
                    return@BiFunction filteredWorkers.size == 1
                } else {
                    return@BiFunction workers.any { worker -> worker.isChecked }
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

}