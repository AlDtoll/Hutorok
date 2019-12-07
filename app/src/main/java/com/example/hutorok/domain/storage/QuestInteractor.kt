package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Quest
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class QuestInteractor : IQuestInteractor {

    private val value = BehaviorSubject.create<Quest>()

    override fun get(): Observable<Quest> = value

    override fun update(quest: Quest) {
        value.onNext(quest)
    }

}