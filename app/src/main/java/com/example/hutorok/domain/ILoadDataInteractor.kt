package com.example.hutorok.domain

import io.reactivex.Observable

interface ILoadDataInteractor {

    fun get(): Observable<Unit>

    fun execute()

    fun saveResult()
}