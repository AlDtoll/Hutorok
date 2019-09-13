package com.example.hutorok.screen.workers_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.IExecuteTaskInteractor
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.IWorkerInteractor
import com.example.hutorok.domain.storage.IWorkersListInteractor
import com.example.hutorok.routing.IScenarioInteractor
import com.example.hutorok.routing.RouteToWorkerInfoScreenInteractor
import com.example.hutorok.routing.Scenario
import io.reactivex.BackpressureStrategy

class WorkersViewModel(
    private val workersListInteractor: IWorkersListInteractor,
    private val workerInteractor: IWorkerInteractor,
    private val routeToWorkerInfoScreenInteractor: RouteToWorkerInfoScreenInteractor,
    private val scenarioInteractor: IScenarioInteractor,
    private val executeTaskInteractor: IExecuteTaskInteractor
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

}