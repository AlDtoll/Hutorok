package com.example.hutorok.routing

class RouteToWorkersScreenInteractor(
    private val router: IRouter
) {

    fun execute() {
        router.routeToWorkersScreen()
    }
}