package com.example.hutorok.screen

import com.example.hutorok.routing.RouteToBuildsScreenInteractor
import com.example.hutorok.routing.RouteToTasksScreenInteractor
import com.example.hutorok.routing.RouteToWorkersScreenInteractor

class StartViewModel(
    private val routeToWorkersScreenInteractor: RouteToWorkersScreenInteractor,
    private val routeToBuildsScreenInteractor: RouteToBuildsScreenInteractor,
    private val routeToTasksScreenInteractor: RouteToTasksScreenInteractor
) : IStartViewModel {

    override fun clickWorkersButton() {
        routeToWorkersScreenInteractor.execute()
    }

    override fun clickBuildsButton() {
        routeToBuildsScreenInteractor.execute()
    }

    override fun clickTasksButton() {
        routeToTasksScreenInteractor.execute()
    }

    override fun clickEndTurnButton() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}