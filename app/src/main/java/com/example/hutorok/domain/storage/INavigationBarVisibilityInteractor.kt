package com.example.hutorok.domain.storage

import io.reactivex.Observable

interface INavigationBarVisibilityInteractor {

    fun get(): Observable<Boolean>
}