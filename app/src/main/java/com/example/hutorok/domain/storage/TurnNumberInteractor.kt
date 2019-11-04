package com.example.hutorok.domain.storage

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class TurnNumberInteractor(
    private val historyInteractor: IHistoryInteractor
) : ITurnNumberInteractor {

    private val item = BehaviorSubject.create<Int>()

    override fun update(turnNumber: Int) {
        item.onNext(turnNumber)
    }

    override fun get(): Observable<Int> = item

    override fun value(): Int? = item.value

    override fun increment() {
        if (item.value == null) {
            update(0)
        }
        item.value?.run {
            val value = this + 1
            update(value)
            historyInteractor.add("Ход: $value")
        }

    }

}