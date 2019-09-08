package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Worker
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class WorkersListInteractor : IWorkersListInteractor {

    private val list = BehaviorSubject.create<List<Worker>>()

    override fun update(workers: List<Worker>) {
        list.onNext(workers)
    }

    override fun get(): Observable<List<Worker>> = list

}