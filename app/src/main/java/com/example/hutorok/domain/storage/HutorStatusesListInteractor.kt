package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Status
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class HutorStatusesListInteractor : IHutorStatusesListInteractor {

    private val list = BehaviorSubject.create<List<Status>>()

    override fun update(statuses: List<Status>) {
        list.onNext(statuses)
    }

    override fun get(): Observable<List<Status>> = list

    override fun add(status: Status) {
        val mutableList = list.value?.toMutableList()
        mutableList?.add(status)
        mutableList?.toList()?.run {
            list.onNext(this)
        }
    }

}