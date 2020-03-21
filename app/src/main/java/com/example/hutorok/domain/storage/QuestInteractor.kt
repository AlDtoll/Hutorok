package com.example.hutorok.domain.storage

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class QuestInteractor : IQuestInteractor {

    private val value = BehaviorSubject.create<Boolean>()

    override fun get(): Observable<Boolean> = value

    override fun update(isQuest: Boolean) {
        value.onNext(isQuest)
    }

}