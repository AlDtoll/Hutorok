package com.example.hutorok.domain.storage

import io.reactivex.Observable

interface IQuestInteractor {

    fun update(isQuest: Boolean)

    fun get(): Observable<Boolean>
}