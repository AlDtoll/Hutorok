package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Worker
import io.reactivex.Observable

interface IWorkersListInteractor {

    fun update(workers: List<Worker>)

    fun get(): Observable<List<Worker>>

    fun refresh()
}