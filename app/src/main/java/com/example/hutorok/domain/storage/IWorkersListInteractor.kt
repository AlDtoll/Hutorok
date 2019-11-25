package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Worker
import io.reactivex.Observable

interface IWorkersListInteractor {

    fun update(workers: MutableList<Worker>)

    fun get(): Observable<MutableList<Worker>>

    fun refresh()

    fun getPreviousWorker()

    fun getNextWorker()

    fun add(worker: Worker)

    fun value(): MutableList<Worker>
}