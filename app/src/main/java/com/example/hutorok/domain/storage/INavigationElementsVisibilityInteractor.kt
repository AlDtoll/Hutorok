package com.example.hutorok.domain.storage

import io.reactivex.Observable

interface INavigationElementsVisibilityInteractor {

    fun get(): Observable<Boolean>
}