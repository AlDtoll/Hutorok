package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Task
import io.reactivex.Observable

interface IEndTasksListInteractor {

    fun update(tasks: List<Task>)

    fun get(): Observable<List<Task>>
}