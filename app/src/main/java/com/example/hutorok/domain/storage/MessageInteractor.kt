package com.example.hutorok.domain.storage

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class MessageInteractor : IMessageInteractor {

    private val value = BehaviorSubject.create<String>()

    override fun get(): Observable<String> = value

    override fun update(message: String) {
        value.onNext(message)
    }

}