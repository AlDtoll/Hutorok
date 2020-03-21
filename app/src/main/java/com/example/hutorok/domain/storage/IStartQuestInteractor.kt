package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Quest
import io.reactivex.Observable

interface IStartQuestInteractor {

    fun update(quest: Quest)

    fun get(): Observable<Quest>
}