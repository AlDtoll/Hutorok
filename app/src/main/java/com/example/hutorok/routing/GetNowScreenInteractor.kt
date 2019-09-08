package com.example.hutorok.routing

import com.example.hutorok.NowScreen
import io.reactivex.Observable

class GetNowScreenInteractor(
    private val router: IRouter
) : IGetNowScreenInteractor {

    override fun execute(): Observable<NowScreen> = router.nowScreen()

}