package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Worker
import io.reactivex.Observable

class MockWorkersListInteractor : IWorkersListInteractor {

    override fun update(workers: List<Worker>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(): Observable<List<Worker>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}