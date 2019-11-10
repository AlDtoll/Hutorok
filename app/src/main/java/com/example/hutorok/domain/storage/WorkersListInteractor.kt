package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Worker
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class WorkersListInteractor(
    private val workerInteractor: IWorkerInteractor
) : IWorkersListInteractor {

    private val list = BehaviorSubject.create<MutableList<Worker>>()

    override fun update(workers: MutableList<Worker>) {
        list.onNext(workers)
    }

    override fun get(): Observable<MutableList<Worker>> = list

    override fun refresh() {
        list.onNext(list.value ?: mutableListOf())
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

    override fun add(worker: Worker) {
        if (list.value == null) {
            update(mutableListOf())
        }
        list.value?.run {
            this.add(worker)
            update(this)

        }
    }

}