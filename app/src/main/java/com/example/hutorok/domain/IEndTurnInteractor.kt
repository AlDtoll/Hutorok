package com.example.hutorok.domain

import io.reactivex.Observable


interface IEndTurnInteractor {

    fun execute()

    fun get(): Observable<Unit>
}