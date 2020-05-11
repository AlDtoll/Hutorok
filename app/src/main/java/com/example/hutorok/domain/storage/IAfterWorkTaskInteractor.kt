package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Task
import io.reactivex.Observable

interface IAfterWorkTaskInteractor {

    fun update(task: Task)

    fun get(): Observable<Task>

}