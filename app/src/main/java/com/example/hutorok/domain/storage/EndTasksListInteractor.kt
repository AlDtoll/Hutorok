package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Task
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class EndTasksListInteractor : IEndTasksListInteractor {

    private val list = BehaviorSubject.create<List<Task>>()

    override fun update(tasks: List<Task>) {
        list.onNext(tasks)
    }

    override fun get(): Observable<List<Task>> = list
}