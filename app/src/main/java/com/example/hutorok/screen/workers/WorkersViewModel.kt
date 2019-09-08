package com.example.hutorok.screen.workers

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.IWorkerInteractor
import com.example.hutorok.domain.storage.IWorkersListInteractor
import io.reactivex.BackpressureStrategy

class WorkersViewModel(
    private val workersListInteractor: IWorkersListInteractor,
    private val workerInteractor: IWorkerInteractor
) : IWorkersViewModel {

    override fun workersData(): LiveData<List<Worker>> {
        return LiveDataReactiveStreams.fromPublisher(
            workersListInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun clickWorker(worker: Worker) {
        workerInteractor.update(worker)
    }

}