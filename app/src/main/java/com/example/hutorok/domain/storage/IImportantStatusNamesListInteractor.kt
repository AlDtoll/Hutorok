package com.example.hutorok.domain.storage

import io.reactivex.Observable

interface IImportantStatusNamesListInteractor {

    fun update(statuses: List<String>)

    fun get(): Observable<List<String>>
}