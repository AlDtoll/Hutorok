package com.example.hutorok.screen.workers

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.IWorkersListInteractor
import io.reactivex.BackpressureStrategy

class WorkersViewModel(
    private val workersListInteractor: IWorkersListInteractor
) : IWorkersViewModel {

    override fun workersData(): LiveData<List<Worker>> {
        return LiveDataReactiveStreams.fromPublisher(
            workersListInteractor.get().toFlowable(BackpressureStrategy.LATEST)
        )
    }

    override fun clickWorker(worker: Worker) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}