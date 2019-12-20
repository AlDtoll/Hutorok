package com.example.hutorok.routing

class RouteToFinishScreenInteractor(
    private val router: IRouter
) {

    fun execute() {
        router.routeToFinishScreen()
    }
}