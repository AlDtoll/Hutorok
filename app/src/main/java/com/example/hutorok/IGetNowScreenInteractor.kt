package com.example.hutorok

import io.reactivex.Observable

interface IGetNowScreenInteractor {

    fun execute(): Observable<NowScreen>
}