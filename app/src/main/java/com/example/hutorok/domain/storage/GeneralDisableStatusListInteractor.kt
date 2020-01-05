package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Task
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class GeneralDisableStatusListInteractor : IGeneralDisableStatusListInteractor {

    private val list = BehaviorSubject.create<List<Triple<String, Task.Symbol, Double>>>()

    override fun update(conditions: List<Triple<String, Task.Symbol, Double>>) {
        list.onNext(conditions)
    }

    override fun get(): Observable<List<Triple<String, Task.Symbol, Double>>> = list

}