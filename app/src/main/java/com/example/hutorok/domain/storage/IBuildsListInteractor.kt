package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Status
import io.reactivex.Observable

interface IBuildsListInteractor {

    fun update(statuses: MutableList<Status>)

    fun get(): Observable<MutableList<Status>>

    fun value(): MutableList<Status>
}