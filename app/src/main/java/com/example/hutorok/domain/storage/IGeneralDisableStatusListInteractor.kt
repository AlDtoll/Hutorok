package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Task
import io.reactivex.Observable

interface IGeneralDisableStatusListInteractor {

    fun update(conditions: List<Triple<String, Task.Symbol, Double>>)

    fun get(): Observable<List<Triple<String, Task.Symbol, Double>>>
}