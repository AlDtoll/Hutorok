package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Worker
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class WorkerInteractor : IWorkerInteractor {

    private val item = BehaviorSubject.create<Worker>()

    override fun update(worker: Worker) {
        item.onNext(worker)
    }

    override fun get(): Observable<Worker> = item

    override fun value(): Worker? = item.value

}