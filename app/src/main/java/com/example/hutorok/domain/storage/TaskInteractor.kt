package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Task
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class TaskInteractor : ITaskInteractor {

    private val value = BehaviorSubject.create<Task>()

    override fun update(task: Task) {
        value.onNext(task)
    }

    override fun get(): Observable<Task> = value

}