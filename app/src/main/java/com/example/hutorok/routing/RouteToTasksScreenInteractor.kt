package com.example.hutorok.routing

class RouteToTasksScreenInteractor(
    private val router: IRouter
) {

    fun execute() {
        router.routeToTasksScreen()
    }
}