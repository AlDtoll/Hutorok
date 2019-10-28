package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Worker
import io.reactivex.Observable

interface IWorkerInteractor {

    fun update(worker: Worker)

    fun get(): Observable<Worker>

    fun value(): Worker?
}