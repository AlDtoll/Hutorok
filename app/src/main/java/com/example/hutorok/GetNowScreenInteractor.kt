package com.example.hutorok

import com.example.hutorok.routing.IRouter
import io.reactivex.Observable

class GetNowScreenInteractor(
    private val router: IRouter
) : IGetNowScreenInteractor {

    override fun execute(): Observable<NowScreen> = router.nowScreen()

}