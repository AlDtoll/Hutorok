package com.example.hutorok.domain.storage

import com.example.hutorok.NowScreen
import com.example.hutorok.routing.IGetNowScreenInteractor
import io.reactivex.Observable

class NavigationElementsVisibilityInteractor(
    private val nowScreenInteractor: IGetNowScreenInteractor
) : INavigationElementsVisibilityInteractor {

    override fun get(): Observable<Boolean> {
        return nowScreenInteractor.execute().map {
            it != NowScreen.QUEST_SCREEN && it != NowScreen.FINISH_SCREEN && it != NowScreen.START_SCREEN
        }
    }

}