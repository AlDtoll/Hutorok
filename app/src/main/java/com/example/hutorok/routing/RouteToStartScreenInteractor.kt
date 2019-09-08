package com.example.hutorok.routing

class RouteToStartScreenInteractor(
    private val router: IRouter
) {

    fun execute() {
        router.routeToStartScreen()
    }
}