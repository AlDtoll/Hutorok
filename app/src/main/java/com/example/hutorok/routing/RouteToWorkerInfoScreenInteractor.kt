package com.example.hutorok.routing

class RouteToWorkerInfoScreenInteractor(
    private val router: IRouter
) {

    fun execute() {
        router.routeToWorkerInfoScreen()
    }
}