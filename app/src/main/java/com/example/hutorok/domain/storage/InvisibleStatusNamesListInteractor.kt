package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Worker
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class InvisibleStatusNamesListInteractor(
    private val workersListInteractor: IWorkersListInteractor
) : IInvisibleStatusNamesListInteractor {

    private val list = BehaviorSubject.create<MutableList<String>>()

    override fun update(statuses: MutableList<String>) {
        list.onNext(statuses)
        workersListInteractor.value().map { worker ->
            if (!isWorkerVisible(worker, statuses)) {
                worker.isInvisible = true
            }
        }
    }

    override fun get(): Observable<MutableList<String>> = list

    override fun value(): MutableList<String> = list.value ?: emptyList<String>().toMutableList()

    override fun refresh() {
        update(list.value ?: mutableListOf())
    }

    private fun isWorkerVisible(worker: Worker, codes: List<String>): Boolean {
        codes.forEach { code ->
            worker.statuses.forEach { status ->
                if (status.isCoincide(code)) {
                    return false
                }
            }
        }
        return true
    }
}