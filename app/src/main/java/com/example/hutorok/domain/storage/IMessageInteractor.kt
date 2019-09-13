package com.example.hutorok.domain.storage

import io.reactivex.Observable

interface IMessageInteractor {

    fun update(message: String)

    fun get(): Observable<String>
}