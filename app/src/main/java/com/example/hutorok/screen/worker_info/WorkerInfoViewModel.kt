package com.example.hutorok.screen.worker_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.domain.storage.IWorkerInteractor
import io.reactivex.BackpressureStrategy

class WorkerInfoViewModel(
    private val workerInteractor: IWorkerInteractor
) : IWorkerInfoViewModel {

    override fun workerData(): LiveData<Worker> = LiveDataReactiveStreams.fromPublisher(
        workerInteractor.get().toFlowable(BackpressureStrategy.LATEST)
    )

    override fun statusesData(): LiveData<List<Status>> {
        val observable = workerInteractor.get().map {
            it.statuses.filter { status -> status.visible }
        }
        return LiveDataReactiveStreams.fromPublisher(
            observable.toFlowable(BackpressureStrategy.LATEST)
        )
    }

}