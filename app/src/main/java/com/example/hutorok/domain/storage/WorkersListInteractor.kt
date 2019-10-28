package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Worker
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class WorkersListInteractor(
    private val workerInteractor: IWorkerInteractor
) : IWorkersListInteractor {

    private val list = BehaviorSubject.create<List<Worker>>()

    override fun update(workers: List<Worker>) {
        list.onNext(workers)
    }

    override fun get(): Observable<List<Worker>> = list

    override fun refresh() {
        list.onNext(list.value ?: emptyList())
    }

    override fun getPreviousWorker() {
        list.value?.run {
            val indexOf = this.indexOf(workerInteractor.value())
            if (indexOf > 0) {
                workerInteractor.update(this[indexOf - 1])
            }
        }
    }

    override fun getNextWorker() {
        list.value?.run {
            val indexOf = this.indexOf(workerInteractor.value())
            if (indexOf < this.size - 1) {
                workerInteractor.update(this[indexOf + 1])
            }
        }
    }

}