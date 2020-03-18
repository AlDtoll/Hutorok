package com.example.hutorok.domain.storage

import com.example.hutorok.NowScreen
import com.example.hutorok.routing.IGetNowScreenInteractor
import io.reactivex.Observable

class NavigationBarVisibilityInteractor(
    private val nowScreenInteractor: IGetNowScreenInteractor
) : INavigationBarVisibilityInteractor {

    override fun get(): Observable<Boolean> {
        return nowScreenInteractor.execute().map {
            it != NowScreen.QUEST_SCREEN && it != NowScreen.FINISH_SCREEN
        }
    }

}