package com.example.hutorok.domain.storage

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class InvisibleStatusNamesListInteractor : IInvisibleStatusNamesListInteractor {

    private val list = BehaviorSubject.create<List<String>>()

    override fun update(statuses: List<String>) {
        list.onNext(statuses)
    }

    override fun get(): Observable<List<String>> = list
}