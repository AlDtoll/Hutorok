package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Status
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class HutorStatusesListInteractor : IHutorStatusesListInteractor {

    private val list = BehaviorSubject.create<MutableList<Status>>()

    override fun update(statuses: MutableList<Status>) {
        list.onNext(statuses)
    }

    override fun get(): Observable<MutableList<Status>> = list

    override fun value(): MutableList<Status> = list.value ?: emptyList<Status>().toMutableList()
}