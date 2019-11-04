package com.example.hutorok.domain.storage

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MessageInteractor(
    private val historyInteractor: IHistoryInteractor
) : IMessageInteractor {

    private val value = PublishSubject.create<String>()

    override fun get(): Observable<String> = value

    override fun update(message: String) {
        value.onNext(message)
        historyInteractor.add(message)
    }

}