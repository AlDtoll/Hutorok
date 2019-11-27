package com.example.hutorok.domain.storage

import io.reactivex.Observable

interface IInvisibleStatusNamesListInteractor {

    fun update(statuses: MutableList<String>)

    fun get(): Observable<MutableList<String>>

    fun value(): MutableList<String>

    fun refresh()
}