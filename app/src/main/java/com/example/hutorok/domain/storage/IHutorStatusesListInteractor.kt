package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Status
import io.reactivex.Observable

interface IHutorStatusesListInteractor {

    fun update(statuses: List<Status>)

    fun get(): Observable<List<Status>>
}