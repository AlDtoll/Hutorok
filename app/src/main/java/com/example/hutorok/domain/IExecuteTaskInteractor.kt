package com.example.hutorok.domain

import io.reactivex.Observable

interface IExecuteTaskInteractor {

    fun get(): Observable<Unit>

    fun execute()
}