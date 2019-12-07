package com.example.hutorok.routing

class RouteToQuestScreenInteractor(
    private val router: IRouter
) {

    fun execute() {
        router.routeToQuestScreen()
    }
}