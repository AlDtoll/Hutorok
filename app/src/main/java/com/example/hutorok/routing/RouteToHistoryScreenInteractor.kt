package com.example.hutorok.routing

class RouteToHistoryScreenInteractor(
    private val router: IRouter
) {

    fun execute() {
        router.routeToHistoryScreen()
    }
}