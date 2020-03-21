package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Adventure
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class AdventuresListInteractor : IAdventuresListInteractor {

    private val list = BehaviorSubject.create<List<Adventure>>()

    override fun update(adventures: List<Adventure>) {
        list.onNext(adventures)
    }

    override fun get(): Observable<List<Adventure>> = list
}