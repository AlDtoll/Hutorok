package com.example.hutorok.routing

import com.example.hutorok.NowScreen
import io.reactivex.Observable

interface IGetNowScreenInteractor {

    fun execute(): Observable<NowScreen>
}