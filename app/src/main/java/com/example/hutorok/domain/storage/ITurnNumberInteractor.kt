package com.example.hutorok.domain.storage

import io.reactivex.Observable

interface ITurnNumberInteractor {

    fun update(turnNumber: Int)

    fun get(): Observable<Int>

    fun value(): Int?

    fun increment()
}