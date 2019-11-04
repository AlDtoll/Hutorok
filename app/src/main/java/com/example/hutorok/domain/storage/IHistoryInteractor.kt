package com.example.hutorok.domain.storage

import io.reactivex.Observable

interface IHistoryInteractor {

    fun update(statuses: MutableList<String>)

    fun get(): Observable<MutableList<String>>

    fun add(message: String)
}